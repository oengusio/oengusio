package app.oengus.service;

import app.oengus.dao.ApplicationRepository;
import app.oengus.dao.ApplicationUserInformationRepository;
import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.constants.SocialPlatform;
import app.oengus.entity.dto.*;
import app.oengus.entity.model.*;
import app.oengus.helper.BeanHelper;
import app.oengus.requests.user.UserUpdateRequest;
import app.oengus.service.login.DiscordService;
import app.oengus.service.login.TwitchService;
import app.oengus.service.login.TwitterLoginService;
import app.oengus.service.repository.SubmissionRepositoryService;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.JWTUtil;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final DiscordService discordService;
    private final TwitterLoginService twitterLoginService;
    private final TwitchService twitchService;
    private final JWTUtil jwtUtil;
    private final UserRepositoryService userRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final MarathonService marathonService;
    private final ApplicationRepository applicationRepository;
    private final SelectionService selectionService;
    private final ApplicationUserInformationRepository applicationUserInformationRepository;

    @Autowired
    public UserService(
        final DiscordService discordService, final TwitterLoginService twitterLoginService,
        final TwitchService twitchService, final JWTUtil jwtUtil, final UserRepositoryService userRepositoryService,
        final SubmissionRepositoryService submissionRepositoryService, final MarathonService marathonService,
        final SelectionService selectionService,
        final ApplicationUserInformationRepository applicationUserInformationRepository,
        final ApplicationRepository applicationRepository
    ) {
        this.discordService = discordService;
        this.twitterLoginService = twitterLoginService;
        this.twitchService = twitchService;
        this.jwtUtil = jwtUtil;
        this.userRepositoryService = userRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.marathonService = marathonService;
        this.selectionService = selectionService;
        this.applicationUserInformationRepository = applicationUserInformationRepository;
        this.applicationRepository = applicationRepository;
    }

    public Token login(final String host, final LoginRequest request) throws LoginException {
        final String service = request.getService();
        final String code = request.getCode();

        if ((code == null || code.isBlank()) && !service.contains("twitter")) {
            throw new LoginException("Missing code in request");
        }

        final User user;
        switch (service) {
            case "discord":
                user = this.discordService.login(code, host);
                break;
            case "twitch":
                user = this.twitchService.login(code, host);
                break;
            case "twitterAuth":
                return new Token(this.twitterLoginService.generateAuthUrlForLogin(host));
            case "twitter":
                user = this.twitterLoginService.login(request.getOauthToken(), request.getOauthVerifier());
                break;
            default:
                throw new LoginException("UNKNOWN_SERVICE");
        }

        if (!user.isEnabled()) {
            throw new LoginException("DISABLED_ACCOUNT");
        }

        return new Token(this.jwtUtil.generateToken(user));
    }

    public Object sync(final String host, final LoginRequest request) throws LoginException {
        final String service = request.getService();
        final String code = request.getCode();

        if ((code == null || code.isBlank()) && !service.contains("twitter")) {
            throw new LoginException("Missing code in request");
        }

        return switch (service) {
            case "discord" -> this.discordService.sync(code, host);
            case "twitch" -> this.twitchService.sync(code, host);
            case "twitterAuth" -> new Token(this.twitterLoginService.generateAuthUrlForSync(host));
            case "twitter" -> this.twitterLoginService.sync(request.getOauthToken(), request.getOauthVerifier());
            default -> throw new LoginException("UNKNOWN_SERVICE");
        };
    }

    public void updateRequest(final int id, final UserUpdateRequest userPatch) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        BeanHelper.copyProperties(userPatch, user, "connections");

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
                        .collect(Collectors.toList());
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
        user.setUsernameJapanese(null);
        user.setDiscordId(null);
        user.setTwitchId(null);
        user.setTwitterId(null);
        user.setMail(null);
//        user.setMail("deleted-user@oengus.io");
        user.setEnabled(false);

        final String randomHash = String.valueOf(Objects.hash(user.getUsername(), user.getId()));

        // "Deleted" is 7 in length
        user.setUsername("Deleted" + randomHash.substring(0, Math.min(25, randomHash.length())));

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

    public User getUser(final int id) throws NotFoundException {
        return this.userRepositoryService.findById(id);
    }

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

        final UserProfileDto userProfileDto = new UserProfileDto();

        BeanUtils.copyProperties(user, userProfileDto);
        userProfileDto.setBanned(user.getRoles().contains(Role.ROLE_BANNED));

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
            )
            .collect(Collectors.toList());

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
        // TODO: what the fuck?
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
}
