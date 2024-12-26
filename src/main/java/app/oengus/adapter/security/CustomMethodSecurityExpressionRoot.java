package app.oengus.adapter.security;

import app.oengus.adapter.security.dto.UserDetailsDto;
import app.oengus.application.MarathonService;
import app.oengus.application.port.persistence.PatreonStatusPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.PatreonPledgeStatus;
import app.oengus.domain.Role;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.schedule.Schedule;
import javassist.NotFoundException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import static app.oengus.domain.Constants.MIN_PATREON_PLEDGE_AMOUNT;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    // Free users always had one schedule, this change is just in the UI for them
    private static final int MAX_SCHEDULE_FREE_USER = 1;
    private static final int MAX_SCHEDULE_PAID_USER = 4;

    private final MarathonService marathonService;
    private final UserPersistencePort userPersistencePort;
    private final PatreonStatusPersistencePort patreonStatusPersistencePort;
    private final SchedulePersistencePort schedulePersistencePort;
    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(final Supplier<Authentication> authentication,
                                              final MarathonService marathonService,
                                              final UserPersistencePort userPersistencePort,
                                              final SchedulePersistencePort schedulePersistencePort,
                                              final PatreonStatusPersistencePort patreonStatusPersistencePort) {
        super(authentication);
        this.marathonService = marathonService;
        this.userPersistencePort = userPersistencePort;
        this.schedulePersistencePort = schedulePersistencePort;
        this.patreonStatusPersistencePort = patreonStatusPersistencePort;
    }

    public boolean isSelf(final int id) {
        final var user = this.getUser();

        if (this.isBanned(user)) {
            return false;
        }

        return user != null && Objects.equals(user.getId(), id);
    }

    public boolean isSelfOrAdmin(final int id) {
        final var user = this.getUser();

        if (this.isAdmin(user)) {
            return true;
        }

        if (this.isBanned(user)) {
            return false;
        }

        return user != null && Objects.equals(user.getId(), id);
    }

    public boolean isAdmin() {
        return this.isAdmin(this.getUser());
    }

    public boolean isAdmin(OengusUser user) {
        if (user == null) {
            return false;
        }

        return user.getRoles().contains(Role.ROLE_ADMIN);
    }

    public boolean isBanned() {
        return this.isBanned(this.getUser());
    }

    public boolean isBanned(OengusUser user) {
        if (user == null) {
            return true;
        }

        return user.getRoles().contains(Role.ROLE_BANNED);
    }

    public boolean isMarathonArchived(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return marathon.getEndDate().plusHours(1).isBefore(ZonedDateTime.now());
    }

    public boolean canHaveTeams() {
        return false;
    }

    // TODO: re-implement when we have teams and applications
    public boolean canUpdateTeam(final int teamId) throws NotFoundException {
        /*final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final Team team = this.teamRepositoryService.getById(teamId);
        final int uId = user.getId();

        return team.getLeaders().stream().anyMatch((u) -> u.getId() == uId) || this.isMarathonMod(team.getMarathon(), user);*/
        return false;
    }

    public boolean applicationsOpen(final int teamId) throws NotFoundException {
        /*final Team team = this.teamRepositoryService.getById(teamId);

        return team.isApplicationsOpen();*/
        return false;
    }

    /**
     * TODO: admin and mod are used interchangeably. Admins should have more power than mods
     */
    public boolean isMarathonAdmin(final String marathonId) throws NotFoundException {
        final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if(this.isBanned(user)) {
            return false;
        }

        if (this.isAdmin(user)) {
            return true;
        }

        final var marathon = this.getMarathon(marathonId);

        return Objects.equals(marathon.getCreator().getId(), user.getId());
    }

    public boolean isMarathonMod(final String marathonId) throws NotFoundException {
        final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if(this.isBanned(user)) {
            return false;
        }

        if (this.isAdmin(user)) {
            return true;
        }

        final Marathon marathon = this.getMarathon(marathonId);

        return this.isMarathonMod(marathon, user);
    }

    public boolean canUpdateMarathon(final String id) throws NotFoundException {
        final var user = this.getUser();
        final Marathon marathon = this.getMarathon(id);

        return this.canUpdateMarathon(marathon, user);
    }

    public boolean canUpdateMarathon(final Marathon marathon, final OengusUser user) {
        if (user == null) {
            return false;
        }

        if (this.isBanned(user)) {
            return false;
        }

        if (this.isAdmin(user)) {
            return true;
        }

        return this.isMarathonMod(marathon, user) && ZonedDateTime.now().isBefore(marathon.getEndDate());
    }

    public boolean isSelectionDone(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return marathon.isSelectionDone();
    }

    // TODO: hasAtLeastOneSchedulePublished(String marathonId)

    public boolean canUpdateMarathonOrIsScheduleDone(final String marathonId) throws NotFoundException {
        final Marathon marathon = this.getMarathon(marathonId);

        if (marathon.isScheduleDone()) {
            return true;
        }

        final var user = this.getUser();

        return this.canUpdateMarathon(marathon, user);
    }

    // TODO: canUpdateMarathonOrIsSchedulePublished (both schedule id and slug)??

    public boolean isSchedulePublished(final String marathonId, final int scheduleId) {
        return this.schedulePersistencePort.findByIdForMarathonWithoutLines(marathonId, scheduleId)
            .map(Schedule::isPublished)
            .orElse(false);

    }

    public boolean isSchedulePublished(final String marathonId, final String slug) {
        return this.schedulePersistencePort.findBySlugForMarathon(marathonId, slug)
            .map(Schedule::isPublished)
            .orElse(false);

    }

    public boolean canCreateExtraSchedule(final String marathonId) {
        final var scheduleCount = this.schedulePersistencePort.getScheduleCountForMarathon(marathonId);
        final var isSupporter = this.isMarathonCreatorSupporter(marathonId);

        if (isSupporter) {
            return scheduleCount < MAX_SCHEDULE_PAID_USER;
        }

        return scheduleCount < MAX_SCHEDULE_FREE_USER;
    }

    public boolean canEditSubmissions(final String marathonId) throws NotFoundException {
        final Marathon marathon = this.getMarathon(marathonId);

        return marathon.isCanEditSubmissions();
    }

    public boolean areSubmissionsOpen(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return (marathon.isCanEditSubmissions() && marathon.isSubmissionsOpen() &&
            ZonedDateTime.now().isBefore(marathon.getEndDate())) ||
            (marathon.getSubmissionsEndDate() != null && ZonedDateTime.now().isBefore(marathon.getSubmissionsEndDate()));
    }

    private boolean isMarathonMod(Marathon marathon, OengusUser user) {
        final int uId = user.getId();

        return marathon.getCreator().getId() == uId ||
            marathon.getModerators().stream().anyMatch((u) -> u.getId() == uId);
    }

    @Nullable
    public OengusUser getUser() {
        if (this.isAnonymous()) {
            return null;
        }

        if (this.getPrincipal() instanceof final UserDetailsDto tmp) {
            // fetch an up-to-date user to make sure we have the correct roles
            return this.userPersistencePort.getById(tmp.id());
        }

        return null;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setFilterObject(final Object obj) {
        this.filterObject = obj;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public void setReturnObject(final Object obj) {
        this.returnObject = obj;
    }

    @Override
    public Object getThis() {
        return this;
    }

    private Marathon getMarathon(String marathonId) throws NotFoundException {
        return this.marathonService.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );
    }

    private boolean isMarathonCreatorSupporter(String marathonId) {
        final var marathonCreator = this.marathonService.findCreatorById(marathonId);

        if (marathonCreator.isPresent()) {
            final var creator = marathonCreator.get();

            // Sponsors are different from patrons
            if (creator.getRoles().contains(Role.ROLE_SPONSOR) || creator.getRoles().contains(Role.ROLE_ADMIN)) {
                return true;
            }

            // TODO: use UserService#getSupporterStatus? Current impl saves a database call if sponsor role is found.
            final var userPatreonId = creator.getPatreonId();

            if (userPatreonId != null && !userPatreonId.isBlank()) {
                final var patreonStatus = this.patreonStatusPersistencePort.findByPatreonId(creator.getPatreonId());

                if (patreonStatus.isPresent()) {
                    final var status = patreonStatus.get();

                    // Pledge amount is in cents, supporters need to at least pledge €1
                    return status.getStatus() == PatreonPledgeStatus.ACTIVE_PATRON && status.getPledgeAmount() >= MIN_PATREON_PLEDGE_AMOUNT;
                }
            }
        }

        return false;
    }
}
