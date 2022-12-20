package app.oengus.service;

import app.oengus.dao.CategoryRepository;
import app.oengus.entity.dto.AvailabilityDto;
import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.entity.dto.misc.PageDto;
import app.oengus.entity.dto.v1.answers.AnswerDto;
import app.oengus.entity.dto.v1.submissions.SubmissionDto;
import app.oengus.entity.dto.v1.submissions.SubmissionUserDto;
import app.oengus.entity.dto.v2.marathon.SubmissionToplevelDto;
import app.oengus.entity.model.*;
import app.oengus.exception.OengusBusinessException;
import app.oengus.exception.SubmissionsClosedException;
import app.oengus.helper.OengusConstants;
import app.oengus.service.repository.*;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static app.oengus.service.CategoryService.MULTIPLAYER_RUN_TYPES;

@Service
public class SubmissionService {

    private final SubmissionRepositoryService submissionRepositoryService;
    private final MarathonRepositoryService marathonRepositoryService;
    private final SelectionRepositoryService selectionRepositoryService;
    private final UserRepositoryService userRepositoryService;
    private final CategoryRepository categoryRepository;
    private final GameRepositoryService gameRepositoryService;
    private final OengusWebhookService webhookService;

    public SubmissionService(
        SubmissionRepositoryService submissionRepositoryService, MarathonRepositoryService marathonRepositoryService,
        SelectionRepositoryService selectionRepositoryService, UserRepositoryService userRepositoryService,
        CategoryRepository categoryRepository, GameRepositoryService gameRepositoryService,
        @Lazy OengusWebhookService webhookService
    ) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.marathonRepositoryService = marathonRepositoryService;
        this.selectionRepositoryService = selectionRepositoryService;
        this.userRepositoryService = userRepositoryService;
        this.categoryRepository = categoryRepository;
        this.gameRepositoryService = gameRepositoryService;
        this.webhookService = webhookService;
    }

    ///////////
    // v2 stuff

    public SubmissionToplevelDto getToplevelSubmissionsForMarathon(final String marathonId) {
        final var dto = new SubmissionToplevelDto();
        final Marathon marathon = new Marathon();

        marathon.setId(marathonId);

        final var submissionData = this.submissionRepositoryService.getToplevelDataForMarathon(marathon);

        dto.setData(submissionData);

        return dto;
    }

    ///////////
    // V1 stuff

    public Submission save(final Submission submission, final User submitter, final String marathonId)
        throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(marathonId);

        if (!marathon.isSubmitsOpen()) {
            throw new SubmissionsClosedException();
        }

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

    public Submission update(final Submission newSubmission, final User submitter, final String marathonId)
        throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(marathonId);

        if (!marathon.isSubmitsOpen()) {
            throw new SubmissionsClosedException();
        }

        // submission id is never null here
        final Submission oldSubmission = this.submissionRepositoryService.findById(newSubmission.getId()).fresh(true);

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

    @Transactional
    public Submission saveInternal(final Submission submission, final User submitter, final Marathon marathon) {
        submission.setUser(submitter);
        submission.setMarathon(marathon);
        submission.getAvailabilities().forEach(availability -> {
            availability.setFrom(availability.getFrom().withSecond(0));
            availability.setTo(availability.getTo().withSecond(0));
        });
        submission.getGames().forEach(game -> {
            game.setSubmission(submission);
            game.getCategories().forEach(category -> {
                category.setGame(game);
                if (category.getId() <= 0) {
                    this.createSelection(category, marathon);
                } else {
                    final Selection selection = this.selectionRepositoryService.findByCategory(category);
                    if (selection == null) {
                        this.createSelection(category, marathon);
                    }
                    category.setSelection(selection);
                }
                if (category.getEstimate().toSecondsPart() > 0) {
                    category.setEstimate(category.getEstimate().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES));
                }
                if (MULTIPLAYER_RUN_TYPES.contains(category.getType())) {
                    if (StringUtils.isEmpty(category.getCode())) {
                        String code;
                        do {
                            code = RandomStringUtils.random(6, true, true).toUpperCase();
                        } while (this.categoryRepository.existsByCode(code));
                        category.setCode(code);
                    }
                } else {
                    category.setCode(null);
                }
            });
        });
        submission.getAnswers().forEach(answer -> answer.setSubmission(submission));
        submission.setOpponents(new HashSet<>());
        if (submission.getOpponentDtos() != null) {
            submission.getOpponentDtos().forEach(opponentDto -> {
                final Opponent opponent = new Opponent();
                opponent.setId(opponentDto.getId());
                opponent.setSubmission(submission);
                opponent.setVideo(opponentDto.getVideo());
                final Category category = new Category();
                category.setId(opponentDto.getCategoryId());
                opponent.setCategory(category);
                submission.getOpponents().add(opponent);
            });
        }

        return this.submissionRepositoryService.save(submission);
    }

    public Map<String, List<AvailabilityDto>> getRunnersAvailabilitiesForMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final List<Submission> submissions =
                this.submissionRepositoryService.findValidatedOrBonusSubmissionsForMarathon(marathon);
        final Map<String, List<AvailabilityDto>> availabilities = new HashMap<>();
        submissions.forEach(submission -> {
            final List<AvailabilityDto> availabilityDtoList = new ArrayList<>();
            submission.getAvailabilities().forEach(availability -> {
                final AvailabilityDto availabilityDto =
                        new AvailabilityDto(submission.getUser().getUsername(),
                                submission.getUser().getUsername("ja"));
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
                            opponent.getSubmission().getAvailabilities().forEach(availability -> {
                                final AvailabilityDto availabilityDto =
                                        new AvailabilityDto(opponent.getSubmission().getUser().getUsername(),
                                                opponent.getSubmission().getUser().getUsername("ja"));
                                availabilityDto.setFrom(availability.getFrom());
                                availabilityDto.setTo(availability.getTo());
                                opponentAvailabilityDtoList.add(availabilityDto);
                            });
                            availabilities.put(opponent.getSubmission().getUser().getUsername(),
                                    opponentAvailabilityDtoList);
                        });
                    }
                });
            });
        });
        return availabilities;
    }

    @Transactional
    public Map<String, List<AvailabilityDto>> getRunnerAvailabilitiesForMarathon(final String marathonId,
                                                                                 final int runnerId)
            throws NotFoundException {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final User user =
                this.userRepositoryService.findById(runnerId);
        final Submission submission = this.submissionRepositoryService.findByUserAndMarathon(user, marathon);
        final Map<String, List<AvailabilityDto>> availabilities = new HashMap<>();
        final List<AvailabilityDto> availabilityDtoList = new ArrayList<>();
        if (submission != null) {
            submission.getAvailabilities().forEach(availability -> {
                final AvailabilityDto availabilityDto =
                        new AvailabilityDto(user.getUsername(), user.getUsername("ja"));
                availabilityDto.setFrom(availability.getFrom());
                availabilityDto.setTo(availability.getTo());
                availabilityDtoList.add(availabilityDto);
            });
        }
        availabilities.put(user.getUsername(), availabilityDtoList);
        return availabilities;
    }

    private void createSelection(final Category category, final Marathon marathon) {
        final Selection selection = new Selection();
        selection.setStatus(Status.TODO);
        selection.setMarathon(marathon);
        selection.setCategory(category);
        category.setSelection(selection);
    }

    @Transactional
    public Submission findByUserAndMarathon(final User user, final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final Submission submission = this.submissionRepositoryService.findByUserAndMarathon(user, marathon);

        if (submission != null) {
            if (submission.getOpponents() != null) {
                submission.setOpponentDtos(new HashSet<>());
                submission.getOpponents().forEach(opponent -> {
                    submission.getOpponentDtos().add(this.mapOpponent(opponent, user));
                });
            }

            if (submission.getGames() != null) {
                submission.getGames().forEach(game -> {
                    game.getCategories().forEach(category -> {
                        if (category.getOpponents() != null) {
                            category.setOpponentDtos(new ArrayList<>());
                            category.getOpponents().forEach(opponent -> {
                                final OpponentCategoryDto opponentCategoryDto = new OpponentCategoryDto();
                                opponentCategoryDto.setId(opponent.getId());
                                opponentCategoryDto.setVideo(opponent.getVideo());
                                opponentCategoryDto.setUser(SubmissionUserDto.fromUser(opponent.getSubmission().getUser()));
                                category.getOpponentDtos().add(opponentCategoryDto);
                            });
                        }
                    });
                });
            }
        }

        return submission;
    }

    private OpponentSubmissionDto mapOpponent(final Opponent opponent, final User user) {
        final OpponentSubmissionDto opponentDto = new OpponentSubmissionDto();
        opponentDto.setId(opponent.getId());
        opponentDto.setGameName(opponent.getCategory().getGame().getName());
        opponentDto.setCategoryId(opponent.getCategory().getId());
        opponentDto.setCategoryName(opponent.getCategory().getName());
        opponentDto.setVideo(opponent.getVideo());
        final List<User> users = new ArrayList<>();
        users.add(opponent.getCategory().getGame().getSubmission().getUser());
        users.addAll(opponent.getCategory()
                             .getOpponents()
                             .stream()
                             .map(opponent1 -> opponent1.getSubmission().getUser())
                             .filter(user1 -> !Objects.equals(user1.getId(), user.getId()))
                             .collect(
                                     Collectors.toSet()));
        opponentDto.setUsers(users);
        return opponentDto;
    }

    public List<AnswerDto> findAnswersByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final List<Submission> byMarathon = this.submissionRepositoryService.findAllByMarathon(marathon);
        final List<AnswerDto> answers = new ArrayList<>();

        byMarathon.forEach((submission) -> {
            submission.getAnswers().forEach((answer) -> {
                if (answer.getQuestion().getFieldType() == FieldType.FREETEXT) {
                    return;
                }

                answers.add(AnswerDto.fromAnswer(answer));
            });
        });

        return answers;
    }

    public List<SubmissionDto> searchForMarathon(final String marathonId, final String query, String status) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final String queryLower = query.toLowerCase();

        final List<Submission> bySearch;
        final Status cStat;

        if (status == null) {
            // Fast return if we have nothing to search for
            if (queryLower.isBlank()) {
                return List.of();
            }

            cStat = null;
            bySearch = this.submissionRepositoryService.searchForMarathon(marathon, query);
        } else {
            try {
                cStat = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                throw new OengusBusinessException("Invalid status supplied");
            }
            bySearch = this.submissionRepositoryService.searchForMarathonWithStatus(
                marathon, query, cStat
            );
        }

        final List<SubmissionDto> submissions = new ArrayList<>();
        final boolean qNotEmpty = !queryLower.isBlank();
        final Predicate<User> matchesUsername = (user) ->
            user.getUsername().toLowerCase().contains(queryLower) ||
                (user.getUsernameJapanese() != null && user.getUsernameJapanese().toLowerCase().contains(queryLower));

        bySearch.stream()
            .filter(
                (submission) -> {
                    final User user = submission.getUser();

                    if (qNotEmpty && matchesUsername.test(user)) {
                        return true;
                    }

                    final Set<Game> gameSet = submission.getGames()
                        .stream()
                        .filter((game) -> {
                            if (qNotEmpty && game.getName().toLowerCase().contains(queryLower)) {
                                return true;
                            }

                            final List<Category> categoryList = game.getCategories()
                                .stream()
                                .filter(
                                    (category) -> {
                                        boolean base = true;

                                        if (qNotEmpty) {
                                            if (category.getOpponents() != null) {
                                                base = category.getOpponents()
                                                    .stream()
                                                    .map(Opponent::getSubmission)
                                                    .map(Submission::getUser)
                                                    .anyMatch(matchesUsername);
                                            }

                                            // if we did not find an opponent, search category name
                                            if (!base) {
                                                base = category.getName().toLowerCase().contains(queryLower);
                                            }
                                        }

                                        if (cStat != null && category.getSelection() != null) {
                                            base &= category.getSelection().getStatus() == cStat;
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
            .forEach(
                (submission) -> submissions.add(this.mapSubmissionDto(submission, false))
            );

        return submissions;
    }

    public PageDto<SubmissionDto> findByMarathonNew(final String marathonId, int page) throws NotFoundException {
        // Get the marathon so that we can see if the selections are done
        final Marathon marathon = this.marathonRepositoryService.findById(marathonId);
        final boolean selectionDone = marathon.isSelectionDone();

        final Page<SubmissionDto> byMarathon = this.submissionRepositoryService
            .findByMarathon(marathon, page)
            .map((s) -> this.mapSubmissionDto(s, selectionDone));

        return new PageDto<>(byMarathon);
    }

    @Transactional
    public List<Submission> findAllByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final List<Submission> byMarathon = this.submissionRepositoryService.findAllByMarathon(marathon);

        // load opponents
        byMarathon.forEach((submission) -> {
            submission.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    if (category.getOpponents() != null) {
                        category.setOpponentDtos(new ArrayList<>());
                        category.getOpponents().forEach(opponent -> {
                            final OpponentCategoryDto opponentCategoryDto = new OpponentCategoryDto();
                            opponentCategoryDto.setId(opponent.getId());
                            opponentCategoryDto.setVideo(opponent.getVideo());
                            opponentCategoryDto.setUser(SubmissionUserDto.fromUser(opponent.getSubmission().getUser()));
                            opponentCategoryDto.setAvailabilities(opponent.getSubmission().getAvailabilities());
                            category.getOpponentDtos().add(opponentCategoryDto);
                        });
                    }
                });
            });
        });

        return byMarathon;
    }

    @Transactional
    public List<Submission> findCustomAnswersByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        return this.submissionRepositoryService.findCustomAnswersByMarathon(marathon);
    }

    @Transactional
    public void deleteByMarathon(final Marathon marathon) {
        this.submissionRepositoryService.deleteByMarathon(marathon);
    }

    public boolean userHasSubmitted(final Marathon marathon, final User user) {
        return this.submissionRepositoryService.existsByMarathonAndUser(marathon, user);
    }

    // User is the person deleting the submission
    public void delete(final int id, final User user) throws NotFoundException {
        final Submission submission = this.submissionRepositoryService.findById(id);
        final Marathon marathon = submission.getMarathon();
        if (submission.getUser().getId() == user.getId() || user.getRoles().contains(Role.ROLE_ADMIN) ||
                marathon.getCreator().getId() == user.getId() ||
                marathon.getModerators().stream().anyMatch(u -> u.getId() == user.getId())) {

            // send webhook
            if (StringUtils.isNotEmpty(marathon.getWebhook())) {
                try {
                    /*// load the submission so we can send it
                    Submission.initialize(submission, true);

                    // Detach it from the manager
                    this.entityManager.detach(submission);*/

                    this.webhookService.sendSubmissionDeleteEvent(marathon.getWebhook(), submission.fresh(true), user);
                } catch (IOException e) {
                    LoggerFactory.getLogger(SubmissionService.class).error(e.getMessage());
                }
            }

            submission.getGames().forEach((game) -> {
                this.categoryRepository.deleteAll(game.getCategories());
                this.gameRepositoryService.delete(game.getId());
            });
            this.submissionRepositoryService.delete(id);
        } else {
            throw new OengusBusinessException("NOT_AUTHORIZED");
        }
    }

    private SubmissionDto mapSubmissionDto(Submission submission, boolean selectionDone) {
        final SubmissionDto dto = new SubmissionDto();

        dto.setId(submission.getId());
        dto.setUser(SubmissionUserDto.fromUser(submission.getUser()));

        submission.getGames().forEach((game) -> {
            game.getCategories().forEach((category) -> {
                if (category.getOpponents() != null) {
                    category.setOpponentDtos(new ArrayList<>());
                    category.getOpponents().forEach(opponent -> {
                        final OpponentCategoryDto opponentCategoryDto = new OpponentCategoryDto();
                        opponentCategoryDto.setId(opponent.getId());
                        opponentCategoryDto.setVideo(opponent.getVideo());
                        opponentCategoryDto.setUser(SubmissionUserDto.fromUser(opponent.getSubmission().getUser()));
                        opponentCategoryDto.setAvailabilities(opponent.getSubmission().getAvailabilities());
                        category.getOpponentDtos().add(opponentCategoryDto);
                    });
                }

                if (selectionDone) {
                    category.setStatus(category.getSelection().getStatus());
                }
            });
        });

        dto.setGames(submission.getGames());

        return dto;
    }
}
