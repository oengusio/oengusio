package app.oengus.service;

import app.oengus.dao.CategoryRepository;
import app.oengus.entity.dto.AvailabilityDto;
import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.entity.model.*;
import app.oengus.exception.OengusBusinessException;
import app.oengus.helper.OengusConstants;
import app.oengus.service.repository.*;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static app.oengus.service.CategoryService.MULTIPLAYER_RUN_TYPES;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    private MarathonRepositoryService marathonRepositoryService;

    @Autowired
    private SelectionRepositoryService selectionRepositoryService;

    @Autowired
    private UserRepositoryService userRepositoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameRepositoryService gameRepositoryService;

    @Autowired
    private OengusWebhookService webhookService;

    @Autowired
    private EntityManager entityManager;

    public Submission save(final Submission submission, final User submitter, final String marathonId)
        throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(marathonId);
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
        // submission id is never null here
        final Submission oldSubmission = this.submissionRepositoryService.findById(newSubmission.getId());

        Submission.initialize(oldSubmission, true);

        // uncache the old submission
        entityManager.detach(oldSubmission);

        // save before sending so we catch the error if saving fails
        final Submission saved = this.saveInternal(newSubmission, submitter, marathon);

        // send webhook
        if (StringUtils.isNotEmpty(marathon.getWebhook())) {
            try {
                this.webhookService.sendSubmissionUpdateEvent(marathon.getWebhook(), newSubmission, oldSubmission);
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
                                opponentCategoryDto.setUser(opponent.getSubmission().getUser());
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

    @Transactional
    public List<Submission> findByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final List<Submission> byMarathon = this.submissionRepositoryService.findByMarathon(marathon);

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
                            opponentCategoryDto.setUser(opponent.getSubmission().getUser());
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
                    // load the submission so we can send it
                    Submission.initialize(submission, true);

                    // Detach it from the manager
                    this.entityManager.detach(submission);

                    this.webhookService.sendSubmissionDeleteEvent(marathon.getWebhook(), submission, user);
                } catch (IOException e) {
                    LoggerFactory.getLogger(SubmissionService.class).error(e.getMessage());
                }
            }

            submission.getGames().forEach((game) -> {
                game.getCategories().forEach(this.categoryRepository::delete);
                this.gameRepositoryService.delete(game.getId());
            });
            this.submissionRepositoryService.delete(id);
        } else {
            throw new OengusBusinessException("NOT_AUTHORIZED");
        }
    }
}
