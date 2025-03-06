package app.oengus.application;

import app.oengus.adapter.rest.dto.SyncDto;
import app.oengus.adapter.rest.dto.v1.request.LoginRequest;
import app.oengus.application.port.persistence.*;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.PatreonPledgeStatus;
import app.oengus.domain.Role;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.Status;
import app.oengus.domain.submission.Submission;
import app.oengus.domain.user.SubmissionHistoryEntry;
import app.oengus.domain.user.SupporterStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static app.oengus.domain.Constants.MIN_PATREON_PLEDGE_AMOUNT;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserSecurityPort securityPort;
    private final UserPersistencePort userPersistencePort;
    private final MarathonPersistencePort marathonPersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final OpponentPersistencePort opponentPersistencePort;
    private final PatreonStatusPersistencePort patreonStatusPersistencePort;
    private final DiscordService discordService;
    private final TwitchService twitchService;

    // TODO: move these all to the lookup service
    public Optional<OengusUser> findByUsername(final String username) {
        return this.userPersistencePort.findByUsername(username);
    }

    public List<OengusUser> searchByUsername(String username) {
        return this.userPersistencePort.findEnabledByUsername(username);
    }

    public Optional<OengusUser> findByEmail(final String email) {
        return this.userPersistencePort.findByEmail(email);
    }

    public List<SubmissionHistoryEntry> getSubmissionHistory(final int userId) {
        final Map<String, Marathon> marathonCache = new HashMap<>();
        final Map<String, List<Submission>> marathonSubmissionMap = new HashMap<>();

        // TODO: this needs pagination so we can have a "load more" button on the profile
        // Somehow this should be a singular query, oh well :D
        final var submissionMap = this.submissionPersistencePort.findByUser(userId)
            .stream()
            .collect(Collectors.groupingBy(Submission::getMarathonId));

        // TODO: can this be made more efficient?
        this.opponentPersistencePort.findParentSubmissionsForUser(userId).forEach((os) -> {
            final var subList = submissionMap.get(os.getMarathonId());

            subList.add(os);
        });


//        final var opponentStream = this.opponentPersistencePort.findParentSubmissionsForUser(userId).stream();
//        final var userOwnSubmissionStream = this.submissionPersistencePort.findByUser(userId).stream();

        // We need to combine some data in order for this to work!
        return submissionMap.values()
            .stream()
            .flatMap(Collection::stream)
            .map((submission) -> {
                final var marathon = marathonCache.computeIfAbsent(
                    submission.getMarathonId(), (marathonId) -> this.marathonPersistencePort.findById(marathonId).get()
                );

                final var entry = new SubmissionHistoryEntry();

                entry.setMarathon(marathon);
                entry.setGames(submission.getGames());

                // strip the status if the pics have not been published yet
                if (!marathon.isSelectionDone()) {
                    entry.getGames().forEach(
                        (game) -> game.getCategories().forEach((category) -> {
                            final var sel = category.getSelection();

                            if (sel != null) {
                                sel.setStatus(Status.TODO);
                            }

                            category.setCode(null);
                        })
                    );
                }

                return entry;
            })
            // Remove private events
            .filter((submission) -> !submission.getMarathon().isPrivate())
            .sorted((a, b) -> b.getMarathon().getStartDate().compareTo(a.getMarathon().getStartDate()))
            .toList();
    }

    public List<Marathon> getModeratedHistory(final int userId) {
        return this.marathonPersistencePort.findAllModeratedBy(userId);
    }

    public SupporterStatus getSupporterStatus(final OengusUser user) {
        final boolean patreonStatus;

        if (user.getPatreonId() == null || user.getPatreonId().isBlank()) {
            patreonStatus = false;
        } else {
            patreonStatus = this.patreonStatusPersistencePort.findByPatreonId(user.getPatreonId())
                .map(
                    // Pledge amount is in cents, supporters need to at least pledge €1
                    (status) -> status.getStatus() == PatreonPledgeStatus.ACTIVE_PATRON && status.getPledgeAmount() >= MIN_PATREON_PLEDGE_AMOUNT
                )
                .orElse(false);
        }

        return SupporterStatus.builder()
            .sponsor(user.getRoles().contains(Role.ROLE_SPONSOR))
            .patron(patreonStatus)
            .build();
    }

    public boolean existsByUsername(String name) {
        return this.userPersistencePort.existsByUsername(name)
            || "new".equalsIgnoreCase(name)
            || "settings".equalsIgnoreCase(name);
    }

    public OengusUser save(final OengusUser user) {
        return this.userPersistencePort.save(user);
    }

    public void setLastLoginToNow(final OengusUser user) {
        user.setLastLogin(ZonedDateTime.now(ZoneOffset.UTC));
        this.save(user);
    }

    // TODO: test if user exists for these two?
    public void addRole(final int id, final Role role) {
        this.userPersistencePort.addRole(id, role);
    }

    public void removeRole(final int id, final Role role) {
        this.userPersistencePort.removeRole(id, role);
    }

    public void markDeleted(int userId) {
        final var user = this.userPersistencePort.getById(userId);

        if (user == null) {
            return; // TODO: return error
        }

        // TODO: delete all connections manually?

        user.getConnections().clear();
        user.setDiscordId(null);
        user.setTwitchId(null);
        user.setTwitterId(null);
        user.setEnabled(false);
        user.setEmailVerified(false);
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        user.setPassword(null);
        user.setRoles(Set.of(
            Role.ROLE_BANNED
        ));

        final String randomHash = String.valueOf(Objects.hash(user.getUsername(), user.getId()));

        // We need an email or stuff breaks, this anonymizes it.
        user.setEmail(randomHash + "@example.com");

        // "Deleted" is 7 in length
        user.setUsername(
            "Deleted" + randomHash.substring(
                0,
                Math.min(25, randomHash.length())
            )
        );
        user.setDisplayName("Deleted user");

        this.save(user);
    }

    // TODO: reimplement this when we actually do applications.
    /*public ApplicationUserInformation getApplicationInfo(User user) throws NotFoundException {
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
    }*/


    // TODO: move to auth?
    public SyncDto sync(final String host, final LoginRequest request) throws LoginException {
        final String service = request.getService();
        final String code = request.getCode();

        if (code == null || code.isBlank()) {
            throw new LoginException("Missing code in request");
        }

        return switch (service) {
            case "discord" -> this.discordService.sync(code, host);
            case "twitch" -> this.twitchService.sync(code, host);
            case "patreon" -> this.checkPatreonSync(code);
            default -> throw new LoginException("UNKNOWN_SERVICE");
        };
    }

    private SyncDto checkPatreonSync(String id) throws LoginException {
        final var user = this.userPersistencePort.findByPatreonId(id).orElse(null);

        // user == null means that we don't have this account synced in the database
        if (user != null && !Objects.equals(user.getId(), this.securityPort.getAuthenticatedUserId())) {
            throw new LoginException("ACCOUNT_ALREADY_SYNCED");
        }

        return new SyncDto(id, null);
    }
}
