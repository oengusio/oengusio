package app.oengus.service;

import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.repository.ApplicationRepository;
import app.oengus.adapter.rest.dto.v1.UserDto;
import app.oengus.adapter.rest.dto.v2.simple.SimpleCategoryDto;
import app.oengus.adapter.rest.dto.v2.simple.SimpleGameDto;
import app.oengus.adapter.rest.dto.v2.users.ModeratedHistoryDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileHistoryDto;
import app.oengus.application.MarathonService;
import app.oengus.dao.ApplicationUserInformationRepository;
import app.oengus.domain.SocialPlatform;
import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.entity.dto.SelectionDto;
import app.oengus.entity.dto.UserApplicationHistoryDto;
import app.oengus.entity.dto.UserHistoryDto;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.model.*;
import app.oengus.helper.BeanHelper;
import app.oengus.service.mapper.ProfileMapper;
import app.oengus.service.repository.SubmissionRepositoryService;
import app.oengus.service.repository.UserRepositoryService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("oldUserService")
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class UserService {
    private final ProfileMapper profileMapper;
    private final UserRepositoryService userRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final MarathonService marathonService;
    private final ApplicationRepository applicationRepository;
    private final SelectionService selectionService;
    private final ApplicationUserInformationRepository applicationUserInformationRepository;

    public void updateRequest(final int id, final UserDto userPatch) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        BeanHelper.copyProperties(userPatch, user, "connections");

        user.setUsername(user.getUsername().toLowerCase());

        // TODO: extract method to request
        if (userPatch.getConnections() == null || userPatch.getConnections().isEmpty()) {
            user.getConnections().clear();
        } else {
            if (user.getConnections().isEmpty()) {
                // newly added properties
                for (final SocialAccount connection : userPatch.getConnections()) {
                    connection.setUser(user);
                }

                user.setConnections(userPatch.getConnections());
            } else {
                final List<SocialAccount> currentConnections = new ArrayList<>(user.getConnections());
                final List<SocialAccount> updateConnections = new ArrayList<>(userPatch.getConnections());
                final List<SocialAccount> toInsert = new ArrayList<>();

                for (final SocialPlatform platform : SocialPlatform.values()) {
                    final List<SocialAccount> current = currentConnections.stream()
                        .filter((c) -> c.getPlatform() == platform)
                        .toList();
                    final List<SocialAccount> update = updateConnections.stream()
                        .filter((c) -> c.getPlatform() == platform)
                        .collect(Collectors.toList());

                    for (final SocialAccount currentAcc : current) {
                        if (update.isEmpty()) {
                            break;
                        }

                        final SocialAccount updateAcc = update.get(0);

                        currentAcc.setUsername(updateAcc.getUsername());
                        toInsert.add(currentAcc);
                        update.remove(0);
                    }

                    // accounts that are new
                    update.forEach((account) -> {
                        final SocialAccount fresh = new SocialAccount();

                        fresh.setId(-1);
                        fresh.setUser(user);
                        fresh.setPlatform(account.getPlatform());
                        fresh.setUsername(account.getUsername());

                        toInsert.add(fresh);
                    });
                }

                user.setConnections(toInsert);
            }
        }

        this.userRepositoryService.update(user);
    }

    // We need to stop throwing the exceptions and start using optionals.
    @Deprecated
    public User getUser(final int id) throws NotFoundException {
        return this.userRepositoryService.findById(id);
    }

    @Nonnull
    @Deprecated
    public User findByUsername(final String username) throws NotFoundException {
        final User user = this.userRepositoryService.findByUsername(username);

        if (user == null) {
            throw new NotFoundException("Unknown user");
        }

        return user;
    }

    public UserProfileDto getUserProfile(final String username) throws NotFoundException {
        final User user = this.userRepositoryService.findByUsername(username);

        if (user == null) {
            throw new NotFoundException("Unknown user");
        }

        final UserProfileDto userProfileDto = this.profileMapper.toV1Profile(user);

        this.addSubmissionsToProfile(
            userProfileDto,
            this.submissionRepositoryService.findByUser(user)
        );

        final var marathons = this.marathonService.findAllMarathonsIModerate(user.getId());

        userProfileDto.setModeratedMarathons(
            marathons.stream().filter(m -> !m.isPrivate()).collect(Collectors.toList())
        );

        final List<ApplicationEntry> apps = this.applicationRepository.findByUserAndStatus(user, ApplicationStatus.APPROVED);

        if (!apps.isEmpty()) {
            userProfileDto.setVolunteeringHistory(
                apps.stream()
                    .map((application) -> {
                        final UserApplicationHistoryDto history = new UserApplicationHistoryDto();

                        final Team team = application.getTeam();

                        history.setTeamId(team.getId());
                        history.setTeamName(team.getName());

                        final MarathonEntity marathon = team.getMarathon();

                        history.setMarathonName(marathon.getName());
                        history.setMarathonId(marathon.getId());

                        history.setStatus(application.getStatus());

                        return history;
                    })
                    .collect(Collectors.toList())
            );
        }

        return userProfileDto;
    }

    private void addSubmissionsToProfile(UserProfileDto userProfileDto, List<SubmissionEntity> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return;
        }

        final List<SubmissionEntity> filteredSubmissions = submissions.stream()
            .filter(
                (submission) -> submission.getMarathon() != null
            )
            .sorted(
                Comparator.comparing((o) -> ((SubmissionEntity) o).getMarathon().getStartDate()).reversed()
            ).toList();

        final Map<Integer, SelectionDto> selections = this.selectionService.findAllByCategory(filteredSubmissions.stream()
                .flatMap((submission) ->
                    submission.getGames()
                        .stream()
                        .flatMap(
                            (game) -> game.getCategories().stream()
                        )
                )
                .collect(Collectors.toList()));

        filteredSubmissions.forEach((submission) -> {
            final MarathonEntity marathon = submission.getMarathon();

            if (marathon.isPrivate()) {
                return;
            }

            final UserHistoryDto userHistoryDto = new UserHistoryDto();

            userHistoryDto.setMarathonId(marathon.getId());
            userHistoryDto.setMarathonName(marathon.getName());
            userHistoryDto.setMarathonStartDate(marathon.getStartDate());
            userHistoryDto.setGames(new ArrayList<>(submission.getGames()));
            userHistoryDto.setOpponents(new ArrayList<>(submission.getOpponents()));
            userHistoryDto.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    if (marathon.isSelectionDone()) {
                        category.setStatus(selections.get(category.getId()).getStatus());
                    } else {
                        category.setStatus(Status.TODO);
                    }
                });
                game.getCategories().sort(Comparator.comparing(CategoryEntity::getId));
            });

            userHistoryDto.getGames().sort(Comparator.comparing(GameEntity::getId));
            userProfileDto.getHistory().add(userHistoryDto);
        });
    }

    /* ==================== V2 stuff ==================== */
    @Nullable
    public ProfileDto getUserProfileV2(final String username) {
        return this.userRepositoryService.findByUsernameRaw(username)
            .map(this.profileMapper::toProfile)
            .orElse(null);
    }

    public List<ModeratedHistoryDto> getUserModeratedHistory(final int userId) {
        return this.marathonService.findAllMarathonsIModerate(userId)
            .stream()
            .filter((m) -> !m.isPrivate())
            .map((m) -> {
                final ModeratedHistoryDto hist = new ModeratedHistoryDto();

                hist.setMarathonId(m.getId());
                hist.setMarathonName(m.getName());
                hist.setMarathonStartDate(m.getStartDate());

                return hist;
            })
            .toList();
    }

    public List<ProfileHistoryDto> getUserProfileHistory(final int userId) {
        final User user = new User();
        user.setId(userId);

        final List<SubmissionEntity> submissions = this.submissionRepositoryService.findByUser(user);

        final List<SubmissionEntity> filteredSubmissions = submissions.stream()
            .filter(
                (submission) -> submission.getMarathon() != null
            )
            .sorted(
                Comparator.comparing((o) -> ((SubmissionEntity) o).getMarathon().getStartDate()).reversed()
            ).toList();

        final List<CategoryEntity> categories = filteredSubmissions.stream()
            .flatMap((submission) ->
                submission.getGames()
                    .stream()
                    .flatMap(
                        (game) -> game.getCategories().stream()
                    )
            ).toList();


        final Map<Integer, SelectionDto> selections = this.selectionService.findAllByCategory(categories);
        final List<ProfileHistoryDto> history = new ArrayList<>();

        filteredSubmissions.forEach((submission) -> {
            final MarathonEntity marathon = submission.getMarathon();

            if (marathon.getIsPrivate()) {
                return;
            }

            final ProfileHistoryDto historyDto = new ProfileHistoryDto();
            final List<SimpleGameDto> sgames = submission.getGames()
                .stream()
                .map((game) -> {
                    final SimpleGameDto sgame = new SimpleGameDto();

                    sgame.setId(game.getId());
                    sgame.setName(game.getName());

                    final List<SimpleCategoryDto> scats = game.getCategories()
                        .stream()
                        .map((cat) -> {
                            final SimpleCategoryDto scat = new SimpleCategoryDto();

                            scat.setId(cat.getId());
                            scat.setName(cat.getName());
                            scat.setEstimate(cat.getEstimate());
                            scat.setStatus(cat.getStatus());

                            return scat;
                        })
                        .collect(Collectors.toList());

                    sgame.setCategories(scats);

                    return sgame;
                })
                .collect(Collectors.toList());

            historyDto.setMarathonId(marathon.getId());
            historyDto.setMarathonName(marathon.getName());
            historyDto.setMarathonStartDate(marathon.getStartDate());
            historyDto.setGames(sgames);
            // historyDto.setOpponents(new ArrayList<>(submission.getOpponents())); // TODO: do we use this?
            historyDto.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    if (marathon.isSelectionDone()) {
                        category.setStatus(selections.get(category.getId()).getStatus());
                    } else {
                        category.setStatus(Status.TODO);
                    }
                });
                game.getCategories().sort(Comparator.comparing(SimpleCategoryDto::getId));
            });

            historyDto.getGames().sort(Comparator.comparing(SimpleGameDto::getId));

            history.add(historyDto);
        });

        return history;
    }
}
