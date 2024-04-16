package app.oengus.application;

import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import app.oengus.entity.dto.SyncDto;
import app.oengus.service.login.DiscordService;
import app.oengus.service.login.TwitchService;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserSecurityPort securityPort;
    private final UserPersistencePort userPersistencePort;
    private final DiscordService discordService;
    private final TwitchService twitchService;

    @Nullable
    public OengusUser getById(int id) {
        return this.userPersistencePort.getById(id);
    }

    public Optional<OengusUser> findByUsername(final String username) {
        return this.userPersistencePort.findByUsername(username);
    }

    public List<OengusUser> searchByUsername(String username) {
        return List.of();
    }

    public Optional<OengusUser> findByEmail(final String email) {
        return this.userPersistencePort.findByEmail(email);
    }

    public boolean existsByUsername(String name) {
        return this.userPersistencePort.existsByUsername(name)
            || "new".equalsIgnoreCase(name)
            || "settings".equalsIgnoreCase(name);
    }

    public OengusUser save(final OengusUser user) {
        return this.userPersistencePort.save(user);
    }

    // TODO: test if user exists for these two?
    public void addRole(final int id, final Role role) {
        this.userPersistencePort.addRole(id, role);
    }

    public void removeRole(final int id, final Role role) {
        this.userPersistencePort.removeRole(id, role);
    }

    public void markDeleted(int userId) {
        final var user = this.getById(userId);

        if (user == null) {
            return; // TODO: return error
        }

        // TODO: delete all connections

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
            Role.ROLE_USER,
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
        final var user = this.userPersistencePort.findByPatreonId(id).orElseThrow(
            () -> new LoginException("User not found")
        );

        if (!Objects.equals(user.getId(), this.securityPort.getAuthenticatedUserId())) {
            throw new LoginException("ACCOUNT_ALREADY_SYNCED");
        }

        return new SyncDto(id, null);
    }
}
