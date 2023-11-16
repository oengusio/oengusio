package app.oengus.service;

import app.oengus.dao.ApplicationRepository;
import app.oengus.dao.ApplicationUserInformationRepository;
import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.constants.SocialPlatform;
import app.oengus.entity.dto.*;
import app.oengus.entity.dto.v2.simple.SimpleCategoryDto;
import app.oengus.entity.dto.v2.simple.SimpleGameDto;
import app.oengus.entity.dto.v2.users.ModeratedHistoryDto;
import app.oengus.entity.dto.v2.users.ProfileDto;
import app.oengus.entity.dto.v2.users.ProfileHistoryDto;
import app.oengus.entity.model.*;
import app.oengus.helper.BeanHelper;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.login.DiscordService;
import app.oengus.service.login.TwitchService;
import app.oengus.service.mapper.UserMapper;
import app.oengus.service.repository.SubmissionRepositoryService;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.JWTUtil;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final DiscordService discordService;
    private final TwitchService twitchService;
    private final JWTUtil jwtUtil;
    private final UserRepositoryService userRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final MarathonService marathonService;
    private final ApplicationRepository applicationRepository;
    private final SelectionService selectionService;
    private final ApplicationUserInformationRepository applicationUserInformationRepository;

    public Token login(final String host, final LoginRequest request) throws LoginException {
        final String service = request.getService();
        final String code = request.getCode();

        if (code == null || code.isBlank()) {
            throw new LoginException("Missing code in request");
        }

        final User user = switch (service) {
            case "discord" -> this.discordService.login(code, host);
            case "twitch" -> this.twitchService.login(code, host);
            // case "twitter" -> this.twitterLoginService.login(code, host);
            default -> throw new LoginException("UNKNOWN_SERVICE");
        };

        if (!user.isEnabled()) {
            throw new LoginException("DISABLED_ACCOUNT");
        }

        return new Token(this.jwtUtil.generateToken(user));
    }

    public Object sync(final String host, final LoginRequest request) throws LoginException {
        final String service = request.getService();
        final String code = request.getCode();

        if (code == null || code.isBlank()) {
            throw new LoginException("Missing code in request");
        }

        return switch (service) {
            case "discord" -> this.discordService.sync(code, host);
            case "twitch" -> this.twitchService.sync(code, host);
            // case "twitter" -> this.twitterLoginService.sync(code, host);
            case "patreon" -> this.checkPatreonSync(code);
            default -> throw new LoginException("UNKNOWN_SERVICE");
        };
    }

    private SyncDto checkPatreonSync(String id) throws LoginException {
        final User user = this.userRepositoryService.findByPatreonId(id);

        if (user != null && !Objects.equals(user.getId(), PrincipalHelper.getCurrentUser().getId())) {
            throw new LoginException("ACCOUNT_ALREADY_SYNCED");
        }

        return new SyncDto(id, null);
    }

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

    @Deprecated
    public void update(final int id, final User userPatch) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        // overwrite the user's roles to make sure they can't set them themselves
        final List<Role> currentRoles = user.getRoles();
        userPatch.setRoles(currentRoles);

        BeanUtils.copyProperties(userPatch, user);
        this.userRepositoryService.update(user);
    }

    public void markDeleted(final int id) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        // TODO: delete all connections

        user.getConnections().clear();
        user.setDiscordId(null);
        user.setTwitchId(null);
        user.setTwitterId(null);
        user.setMail(null);
//        user.setMail("deleted-user@oengus.io");
        user.setEnabled(false);

        final String randomHash = String.valueOf(Objects.hash(user.getUsername(), user.getId()));

        // "Deleted" is 7 in length
        user.setUsername("Deleted" + randomHash.substring(0, Math.min(25, randomHash.length())));
        user.setDisplayName("Deleted user");

        this.userRepositoryService.save(user);
    }

    public void addRole(final int id, final Role role) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        // only update if the user does not have the role yet
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);

            this.userRepositoryService.update(user);
        }
    }

    public void removeRole(final int id, final Role role) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        // only update if the user does have the role
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);

            this.userRepositoryService.update(user);
        }
    }

    @Deprecated
    public User getUser(final int id) throws NotFoundException {
        return this.userRepositoryService.findById(id);
    }

    @Nonnull
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

        final UserProfileDto userProfileDto = this.userMapper.toV1Profile(user);

        this.addSubmissionsToProfile(
            userProfileDto,
            this.submissionRepositoryService.findByUser(user)
        );

        final List<MarathonBasicInfoDto> marathons = this.marathonService.findAllMarathonsIModerate(user);

        userProfileDto.setModeratedMarathons(
            marathons.stream().filter(m -> !m.getPrivate()).collect(Collectors.toList())
        );

        final List<Application> apps = this.applicationRepository.findByUserAndStatus(user, ApplicationStatus.APPROVED);

        if (!apps.isEmpty()) {
            userProfileDto.setVolunteeringHistory(
                apps.stream()
                    .map((application) -> {
                        final UserApplicationHistoryDto history = new UserApplicationHistoryDto();

                        final Team team = application.getTeam();

                        history.setTeamId(team.getId());
                        history.setTeamName(team.getName());

                        final Marathon marathon = team.getMarathon();

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

    private void addSubmissionsToProfile(UserProfileDto userProfileDto, List<Submission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return;
        }

        final List<Submission> filteredSubmissions = submissions.stream()
            .filter(
                (submission) -> submission.getMarathon() != null
            )
            .sorted(
                Comparator.comparing((o) -> ((Submission) o).getMarathon().getStartDate()).reversed()
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
            final Marathon marathon = submission.getMarathon();

            if (marathon.getIsPrivate()) {
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
                game.getCategories().sort(Comparator.comparing(Category::getId));
            });

            userHistoryDto.getGames().sort(Comparator.comparing(Game::getId));
            userProfileDto.getHistory().add(userHistoryDto);
        });
    }

    public List<User> findUsersWithUsername(final String username) {
        return this.userRepositoryService.findByUsernameContainingIgnoreCase(username);
    }

    public boolean exists(final String name) {
        return this.userRepositoryService.existsByUsername(name) || "new".equalsIgnoreCase(name) ||
            "settings".equalsIgnoreCase(name);
    }

    public ApplicationUserInformation getApplicationInfo(User user) throws NotFoundException {
        return this.applicationUserInformationRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    public ApplicationUserInformation updateApplicationInfo(User user, ApplicationUserInformationDto dto) {
        ApplicationUserInformation infoForUser = this.applicationUserInformationRepository.findByUser(user).orElse(null);

        if (infoForUser == null) {
            infoForUser = new ApplicationUserInformation();
            infoForUser.setId(-1);
            infoForUser.setUser(user);
        }

        BeanHelper.copyProperties(dto, infoForUser);

        return this.applicationUserInformationRepository.save(infoForUser);
    }

    /* ==================== V2 stuff ==================== */
    @Nullable
    public ProfileDto getUserProfileV2(final String username) {
        return this.userRepositoryService.findByUsernameRaw(username)
            .map(this.userMapper::toProfile)
            .orElse(null);
    }

    public List<ModeratedHistoryDto> getUserModeratedHistory(final int userId) {
        final User user = new User();
        user.setId(userId);

        return this.marathonService.findAllMarathonsIModerate(user)
            .stream()
            .filter((m) -> !m.getPrivate())
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

        final List<Submission> submissions = this.submissionRepositoryService.findByUser(user);

        final List<Submission> filteredSubmissions = submissions.stream()
            .filter(
                (submission) -> submission.getMarathon() != null
            )
            .sorted(
                Comparator.comparing((o) -> ((Submission) o).getMarathon().getStartDate()).reversed()
            ).toList();

        final List<Category> categories = filteredSubmissions.stream()
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
            final Marathon marathon = submission.getMarathon();

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
