package app.oengus.application;

import app.oengus.adapter.rest.dto.v2.marathon.GameDto;
import app.oengus.adapter.rest.dto.v2.marathon.SubmissionDto;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.port.persistence.*;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.*;
import app.oengus.adapter.rest.dto.AvailabilityDto;
import app.oengus.domain.marathon.FieldType;
import app.oengus.domain.submission.Status;
import app.oengus.domain.exception.OengusBusinessException;
import app.oengus.domain.exception.SubmissionsClosedException;
import app.oengus.application.helper.OengusConstants;
import app.oengus.domain.Role;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static app.oengus.application.CategoryService.MULTIPLAYER_RUN_TYPES;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionPersistencePort submissionPersistencePort;
    private final MarathonPersistencePort marathonPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;
    private final GamePersistencePort gamePersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final SelectionPersistencePort selectionPersistencePort;

    private final OengusWebhookService webhookService;
    private final UserDtoMapper userMapper;

    @Value("${oengus.pageSize}")
    private int pageSize;

    ///////////
    // v2 stuff

    public List<SubmissionDto> getToplevelSubmissionsForMarathon(final String marathonId) {
        return List.of();
    }

    public List<GameDto> getGamesForSubmission(String marathonId, int submissionId) {
        return List.of();
    }

    ///////////
    // V1 stuff

    public Submission save(final Submission submission, final OengusUser submitter, final String marathonId)
        throws NotFoundException {
        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        if (!marathon.isSubmissionsOpen()) {
            throw new SubmissionsClosedException();
        }

        submission.setUser(submitter);

        final Submission saved = this.saveInternal(submission, submitter, marathon);

        // send webhook
        if (StringUtils.isNotEmpty(marathon.getWebhook())) {
            try {
                this.webhookService.sendNewSubmissionEvent(marathon.getWebhook(), submission);
            } catch (IOException e) {
                LoggerFactory.getLogger(SubmissionService.class).error(e.getLocalizedMessage());
            }
        }

        return saved;
    }

    public Submission update(final Submission newSubmission, final OengusUser submitter, final String marathonId)
        throws NotFoundException {
        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        // submission id is never null here
        final Submission oldSubmission = this.submissionPersistencePort.findById(newSubmission.getId()).orElseThrow(
            () -> new NotFoundException("Submission not found (wat?)")
        );

        this.forceOpponents(oldSubmission, newSubmission);

        // save before sending so we catch the error if saving fails
        final Submission saved = this.saveInternal(newSubmission, submitter, marathon);

        // send webhook
        if (StringUtils.isNotEmpty(marathon.getWebhook())) {
            try {
                this.webhookService.sendSubmissionUpdateEvent(marathon.getWebhook(), saved, oldSubmission);
            } catch (IOException e) {
                LoggerFactory.getLogger(SubmissionService.class).error(e.getLocalizedMessage());
            }
        }

        return saved;
    }

    private void forceOpponents(final Submission oldSubmission, final Submission newSubmission) {
        for (final var game : oldSubmission.getGames()) {
            for (final var category : game.getCategories()) {
                if (category.getType() != RunType.SINGLE) {
                    final var newCategory = this.findByIdInSubmission(newSubmission, category.getId());

                    if (newCategory == null) {
                        continue;
                    }

                    newCategory.setOpponents(category.getOpponents());
                }
            }
        }
    }

    private Category findByIdInSubmission(final Submission submission, final int categoryId) {
        for (var game : submission.getGames()) {
            for (var category : game.getCategories()) {
                if (category.getId() == categoryId) {
                    return category;
                }
            }
        }

        return null;
    }

    public Submission saveInternal(final Submission submission, final OengusUser submitter, final Marathon marathon) {
        submission.getAvailabilities().forEach(availability -> {
            availability.setFrom(availability.getFrom().withSecond(0));
            availability.setTo(availability.getTo().withSecond(0));
        });

        // Fetch all selections in batch, faster and more efficient operation
        final var categoryIds = submission.getGames().stream()
            .flatMap(
                (game) -> game.getCategories()
                    .stream()
                    .map(Category::getId)
                    .filter((id) -> id > 0)
            )
            .toList();

        // Put the selections in a map for O(1) lookup
        final Map<Integer, Selection> selectionMap = new HashMap<>();

        this.selectionPersistencePort.findByCategoryIds(categoryIds).forEach((selection) ->
            selectionMap.put(
                selection.getCategoryId(),
                selection
            )
        );

        // TODO: make sure no new games can be added when submissions are closed
        submission.getGames().forEach(game -> {
            game.getCategories().forEach(category -> {
                if (category.getId() > 0) {
                    category.setSelection(
                        selectionMap.get(category.getId())
                    );
                }

                if (category.getCreatedAt() == null) {
                    category.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
                }

                // Truncate the estimate to minutes?
                if (category.getEstimate().toSecondsPart() > 0) {
                    category.setEstimate(
                        category.getEstimate()
                            .plusMinutes(1)
                            .truncatedTo(ChronoUnit.MINUTES)
                    );
                }

                if (MULTIPLAYER_RUN_TYPES.contains(category.getType())) {
                    if (StringUtils.isEmpty(category.getCode())) {
                        String code;
                        do {
                            code = RandomStringUtils.secure().next(6, true, true).toUpperCase();
                        } while (this.categoryPersistencePort.existsByCode(code));
                        category.setCode(code);
                    }
                } else {
                    category.setCode(null);
                }
            });
        });

        // TODO: is this even needed?
        //  Maybe a check to see if the question id is valid for the marathon
//        submission.setAnswers(
//            submission.getAnswers().stream()
//                .map((oldAnswer) -> {
//                    final var answer = new Answer(
//                        oldAnswer.getId(),
//                        submission.getId()
//                    );
//
//                    answer.setQuestion(oldAnswer.getQuestion());
//                    answer.setAnswer(oldAnswer.getAnswer());
//
//                    return answer;
//                }).collect(Collectors.toSet())
//        );

        // TODO: fix opponents
        /*submission.setOpponents(new HashSet<>());

        if (submission.getOpponentDtos() != null) {
            submission.getOpponentDtos().forEach(opponentDto -> {
                final OpponentEntity opponent = new OpponentEntity();
                opponent.setId(opponentDto.getId());
                opponent.setSubmission(submission);
                opponent.setVideo(opponentDto.getVideo());
                final CategoryEntity category = new CategoryEntity();
                category.setId(opponentDto.getCategoryId());
                opponent.setCategory(category);
                submission.getOpponents().add(opponent);
            });
        }*/

        return this.submissionPersistencePort.save(submission);
    }

    public Map<String, List<AvailabilityDto>> getRunnersAvailabilitiesForMarathon(final String marathonId) {
        final var submissions = this.submissionPersistencePort.findAcceptedInMarathon(marathonId);
        final Map<String, List<AvailabilityDto>> availabilities = new HashMap<>();
        submissions.forEach(submission -> {
            final List<AvailabilityDto> availabilityDtoList = new ArrayList<>();
            submission.getAvailabilities().forEach(availability -> {
                final AvailabilityDto availabilityDto =
                    new AvailabilityDto(submission.getUser());
                availabilityDto.setFrom(availability.getFrom());
                availabilityDto.setTo(availability.getTo());
                availabilityDtoList.add(availabilityDto);
            });
            availabilities.put(submission.getUser().getUsername(), availabilityDtoList);

            submission.getGames().forEach(game -> {
                game.getCategories().forEach(category -> {
                    if (OengusConstants.ACCEPTED_STATUSES.contains(category.getSelection().getStatus())) {
                        category.getOpponents().forEach(opponent -> {
                            final List<AvailabilityDto> opponentAvailabilityDtoList = new ArrayList<>();
                            final var opponentSubmission = this.submissionPersistencePort.findById(opponent.getSubmissionId()).get();

                            opponentSubmission.getAvailabilities().forEach(availability -> {
                                final var availabilityDto = new AvailabilityDto(opponentSubmission.getUser());
                                availabilityDto.setFrom(availability.getFrom());
                                availabilityDto.setTo(availability.getTo());
                                opponentAvailabilityDtoList.add(availabilityDto);
                            });

                            availabilities.put(opponentSubmission.getUser().getUsername(), opponentAvailabilityDtoList);
                        });
                    }
                });
            });
        });

        return availabilities;
    }

    // TODO: do not map models here
    public Map<String, List<AvailabilityDto>> getRunnerAvailabilitiesForMarathon(
        final String marathonId, final int runnerId
    ) {
        final var user = this.userPersistencePort.getById(runnerId);
        final var optionalSubmission = this.submissionPersistencePort.findForUserInMarathon(runnerId, marathonId);
        final Map<String, List<AvailabilityDto>> availabilities = new HashMap<>();
        final List<AvailabilityDto> availabilityDtoList = new ArrayList<>();

        this.submissionPersistencePort.findForUserInMarathon(runnerId, marathonId).ifPresent((submission) -> {
            submission.getAvailabilities().forEach(availability -> {
                final var availabilityDto = new AvailabilityDto(user);

                availabilityDto.setFrom(availability.getFrom());
                availabilityDto.setTo(availability.getTo());

                availabilityDtoList.add(availabilityDto);
            });
        });

        availabilities.put(user.getUsername(), availabilityDtoList);
        return availabilities;
    }

    public Submission findByUserAndMarathon(final int userId, final String marathonId) {
        return this.submissionPersistencePort.findForUserInMarathon(userId, marathonId).orElse(null);

    }

    public List<Answer> findAnswersByMarathon(final String marathonId) {
        final var byMarathon = this.submissionPersistencePort.findAllByMarathon(marathonId);
        final List<Answer> answers = new ArrayList<>();

        byMarathon.forEach((submission) -> {
            submission.getAnswers().forEach((answer) -> {
                if (answer.getQuestion().getFieldType() == FieldType.FREETEXT) {
                    return;
                }

                answers.add(answer);
            });
        });

        return answers;
    }

    public Page<Submission> searchForMarathon(final String marathonId, final String query, String status, int page) {
        final String queryLower = query.toLowerCase(Locale.ROOT);

        final var pageable = PageRequest.of(page, this.pageSize);
        final Page<Submission> bySearch;
        final Status cStat;

        if (status == null) {
            // Fast return if we have nothing to search for
            if (queryLower.isBlank()) {
                return Page.empty();
            }

            cStat = null;
            bySearch = this.submissionPersistencePort.searchInMarathon(
                marathonId, query, pageable
            );
        } else {
            try {
                cStat = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                throw new OengusBusinessException("Invalid status supplied");
            }
            bySearch = this.submissionPersistencePort.searchInMarathon(
                marathonId, query, cStat, pageable
            );
        }

        final boolean qNotEmpty = !queryLower.isBlank();
        final Predicate<OengusUser> matchesUsername = (user) ->
            user.getUsername().toLowerCase(Locale.ROOT).contains(queryLower) ||
                (user.getDisplayName() != null && user.getDisplayName().toLowerCase(Locale.ROOT).contains(queryLower));

        final var opponentSubmissionIds = bySearch.stream()
            .flatMap(
                (s) -> s.getGames()
                    .stream()
                    .flatMap(
                        (g) -> g.getCategories()
                            .stream()
                            .filter((c) -> c.getOpponents() != null && !c.getOpponents().isEmpty())
                            .flatMap((c) -> c.getOpponents().stream())
                            .map(Opponent::getSubmissionId)
                    )
            )
            .sorted(Integer::compare)
            .toList();
        final var opponentUserCache = this.submissionPersistencePort.findUsersByIds(opponentSubmissionIds);

        final var foundItems = bySearch.stream()
            .filter(
                (submission) -> {
                    final var user = submission.getUser();

                    if (qNotEmpty && matchesUsername.test(user)) {
                        return true;
                    }

                    final Set<Game> gameSet = submission.getGames()
                        .stream()
                        .filter((game) -> {
                            if (qNotEmpty && game.getName().toLowerCase(Locale.ROOT).contains(queryLower)) {
                                return true;
                            }

                            final List<Category> categoryList = game.getCategories()
                                .stream()
                                .filter(
                                    (category) -> {
                                        boolean base = false;

                                        if (qNotEmpty) {
                                            final var oppList = category.getOpponents();

                                            if (oppList != null && !oppList.isEmpty()) {
                                                try {
                                                    final var users = oppList.stream()
                                                        .map(Opponent::getSubmissionId)
                                                        .map(opponentUserCache::get)
                                                        .filter(Objects::nonNull)
                                                        .toList();

                                                    base = users.stream().anyMatch(matchesUsername);
                                                } catch (Exception e) {
                                                    log.error("Search failed", e);
                                                }
                                            }

                                            // if we did not find an opponent, search category name
                                            if (!base) {
                                                base = category.getName().toLowerCase(Locale.ROOT).contains(queryLower);
                                            }
                                        }

                                        if (cStat != null && category.getSelection() != null) {
                                            if (qNotEmpty) {
                                                base &= category.getSelection().getStatus() == cStat;
                                            } else {
                                                base = category.getSelection().getStatus() == cStat;
                                            }
                                        }

                                        return base;
                                    }
                                )
                                .toList();

                            game.setCategories(categoryList);

                            return !game.getCategories().isEmpty();
                        })
                        .collect(Collectors.toSet());

                    submission.setGames(gameSet);

                    return !submission.getGames().isEmpty();
                }
            )
            .toList();

        return new PageImpl<>(foundItems, bySearch.getPageable(), bySearch.getTotalElements());
    }

    public Page<Submission> findByMarathonNew(final String marathonId, int page) {
        return this.submissionPersistencePort.findByMarathon(marathonId, PageRequest.of(page, this.pageSize));
    }

    public List<Submission> findAllByMarathon(final String marathonId) {
        return this.submissionPersistencePort.findAllByMarathon(marathonId);
    }

    public void deleteByMarathon(final String marathonId) {
        this.submissionPersistencePort.deleteByMarathon(marathonId);
    }

    public boolean userHasSubmitted(final String marathonId, final int userId) {
        return this.submissionPersistencePort.existsForUserInMarathon(userId, marathonId);
    }

    // User is the person deleting the submission
    public void delete(final int id, final OengusUser deletedBy) throws NotFoundException {
        final Submission submission = this.submissionPersistencePort.findById(id).orElseThrow(
            () -> new NotFoundException("Submission not found")
        );

        final Marathon marathon = this.marathonPersistencePort.findById(submission.getMarathonId()).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        if (submission.getUser().getId() == deletedBy.getId() || deletedBy.getRoles().contains(Role.ROLE_ADMIN) ||
            marathon.getCreator().getId() == deletedBy.getId() ||
            marathon.getModerators().stream().anyMatch(u -> u.getId() == deletedBy.getId())) {

            // send webhook
            if (StringUtils.isNotEmpty(marathon.getWebhook())) {
                try {
                    this.webhookService.sendSubmissionDeleteEvent(marathon.getWebhook(), submission, deletedBy);
                } catch (IOException e) {
                    LoggerFactory.getLogger(SubmissionService.class).error(e.getMessage());
                }
            }

            // Schrödinger's delete
            // Sometimes it works, other times it fails
//            submission.getGames().forEach((game) -> {
////                this.categoryPersistencePort.deleteAllById(
////                    game.getCategories().stream().map(Category::getId).toList()
////                );
//                this.gamePersistencePort.deleteById(game.getId());
//            });
            this.submissionPersistencePort.delete(submission);
        } else {
            throw new OengusBusinessException("NOT_AUTHORIZED");
        }
    }
}
