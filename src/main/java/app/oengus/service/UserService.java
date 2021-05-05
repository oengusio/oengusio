package app.oengus.service;

import app.oengus.entity.dto.MarathonBasicInfoDto;
import app.oengus.entity.dto.SelectionDto;
import app.oengus.entity.dto.UserHistoryDto;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.model.*;
import app.oengus.service.login.DiscordService;
import app.oengus.service.login.TwitchLoginService;
import app.oengus.service.login.TwitchSyncService;
import app.oengus.service.login.TwitterLoginService;
import app.oengus.service.repository.SubmissionRepositoryService;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.JWTUtil;
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

    @Autowired
    private DiscordService discordService;

    @Autowired
    private TwitterLoginService twitterLoginService;

    @Autowired
    private TwitchSyncService twitchSyncService;

    @Autowired
    private TwitchLoginService twitchLoginService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepositoryService userRepositoryService;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    private MarathonService marathonService;

    @Autowired
    private SelectionService selectionService;

    public Token login(final String service, final String code, final String oauthToken, final String oauthVerifier)
            throws LoginException {
        if ((code == null || code.isBlank()) && !service.contains("twitter")) {
            throw new LoginException("Missing code in request");
        }

        final User user;
        switch (service) {
            case "discord":
                user = this.discordService.login(code);
                break;
            case "twitch":
                user = this.twitchLoginService.login(code);
                break;
            case "twitterAuth":
                return new Token(this.twitterLoginService.generateAuthUrlForLogin());
            case "twitter":
                user = this.twitterLoginService.login(oauthToken, oauthVerifier);
                break;
            default:
                throw new LoginException();
        }
        if (!user.isEnabled()) {
            throw new LoginException("DISABLED_ACCOUNT");
        }
        final String token = this.jwtUtil.generateToken(user);
        return new Token(token);
    }

    public Object sync(final String service, final String code, final String oauthToken, final String oauthVerifier)
            throws LoginException {
        if ((code == null || code.isBlank()) && !service.contains("twitter")) {
            throw new LoginException("Missing code in request");
        }

        switch (service) {
            case "discord":
                return this.discordService.sync(code);
            case "twitch":
                return this.twitchSyncService.sync(code);
            case "twitterAuth":
                return new Token(this.twitterLoginService.generateAuthUrlForSync());
            case "twitter":
                return this.twitterLoginService.sync(oauthToken, oauthVerifier);
            default:
                throw new LoginException();
        }
    }

    public void update(final int id, final User userPatch) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);
        BeanUtils.copyProperties(userPatch, user);
        this.userRepositoryService.update(user);
    }

    public void markDeleted(final int id) throws NotFoundException {
        final User user = this.userRepositoryService.findById(id);

        user.setUsernameJapanese(null);
        user.setDiscordId(null);
        user.setDiscordName(null);
        user.setTwitchId(null);
        user.setTwitchName(null);
        user.setTwitterId(null);
        user.setTwitterName(null);
        user.setSpeedruncomName(null);
        user.setMail(null);
//        user.setMail("deleted-user@oengus.io");
        user.setEnabled(false);

        final String randomHash = String.valueOf(Objects.hash(user.getUsername(), user.getId()));

        // "Deleted" is 7 in length
        user.setUsername("Deleted" + randomHash.substring(0, Math.min(7, randomHash.length())));

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

    public UserProfileDto getUserProfile(final String username) {
        final UserProfileDto userProfileDto;
        final User user = this.userRepositoryService.findByUsername(username);
        if (user != null) {
            userProfileDto = new UserProfileDto();
            BeanUtils.copyProperties(user, userProfileDto);
            userProfileDto.setBanned(user.getRoles().contains(Role.ROLE_BANNED));
            final List<Submission> submissions = this.submissionRepositoryService.findByUser(user);
            if (submissions != null && !submissions.isEmpty()) {
                final List<Submission> filteredSubmissions = submissions.stream()
                                                                        .filter(submission ->
                                                                                submission.getMarathon() !=
                                                                                        null)
                                                                        .sorted(Comparator.comparing(
                                                                                o -> ((Submission) o).getMarathon()
                                                                                                     .getStartDate())
                                                                                          .reversed())
                                                                        .collect(Collectors.toList());
                final Map<Integer, SelectionDto> selections =
                        this.selectionService.findAllByCategory(filteredSubmissions.stream()
                                                                                   .flatMap(submission ->
                                                                                           submission.getGames()
                                                                                                     .stream()
                                                                                                     .flatMap(
                                                                                                             game -> game
                                                                                                                     .getCategories()
                                                                                                                     .stream()))
                                                                                   .collect(Collectors.toList()));
                filteredSubmissions.forEach(submission -> {
                    final UserHistoryDto userHistoryDto = new UserHistoryDto();
                    if (!submission.getMarathon().getIsPrivate()) {
                        userHistoryDto.setMarathonId(submission.getMarathon().getId());
                        userHistoryDto.setMarathonName(submission.getMarathon().getName());
                        userHistoryDto.setMarathonStartDate(submission.getMarathon().getStartDate());
                        userHistoryDto.setGames(new ArrayList<>(submission.getGames()));
                        userHistoryDto.setOpponents(new ArrayList<>(submission.getOpponents()));
                        userHistoryDto.getGames()
                                      .forEach(game -> {
                                          game.getCategories()
                                              .forEach(category -> {
                                                  if (submission.getMarathon().isSelectionDone()) {
                                                      category.setStatus(
                                                              selections.get(category.getId()).getStatus());
                                                  } else {
                                                      category.setStatus(Status.TODO);
                                                  }
                                              });
                                          game.getCategories().sort(Comparator.comparing(Category::getId));
                                      });
                        userHistoryDto.getGames().sort(Comparator.comparing(Game::getId));
                        userProfileDto.getHistory().add(userHistoryDto);
                    }
                });
            }
            final List<MarathonBasicInfoDto> marathons = this.marathonService.findAllMarathonsIModerate(user);
            userProfileDto.setModeratedMarathons(
                    marathons.stream().filter(m -> !m.getPrivate()).collect(Collectors.toList()));
        } else {
            userProfileDto = null;
        }
        return userProfileDto;
    }

    public List<User> findUsersWithUsername(final String username) {
        return this.userRepositoryService.findByUsernameContainingIgnoreCase(username);
    }

    public boolean exists(final String name) {
        return this.userRepositoryService.existsByUsername(name) || "new".equalsIgnoreCase(name) ||
                "settings".equalsIgnoreCase(name);
    }
}
