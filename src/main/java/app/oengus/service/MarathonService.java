package app.oengus.service;

import app.oengus.entity.dto.MarathonBasicInfoDto;
import app.oengus.entity.dto.MarathonDto;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import app.oengus.entity.model.User;
import app.oengus.helper.BeanHelper;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.DonationExtraDataRepositoryService;
import app.oengus.service.repository.DonationRepositoryService;
import app.oengus.service.repository.MarathonRepositoryService;
import app.oengus.service.twitter.AbstractTwitterService;
import javassist.NotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarathonService {

    private final MarathonRepositoryService marathonRepositoryService;
    private final SubmissionService submissionService;
    private final ScheduleService scheduleService;
    private final IncentiveService incentiveService;
    private final DonationRepositoryService donationRepositoryService;
    private final DonationExtraDataRepositoryService donationExtraDataRepositoryService;
    private final EntityManager entityManager;
    private final EventSchedulerService eventSchedulerService;
    private final AbstractTwitterService twitterService;
    private final SelectionService selectionService;
    private final OengusWebhookService webhookService;

    public MarathonService(
        SubmissionService submissionService, MarathonRepositoryService marathonRepositoryService,
        ScheduleService scheduleService, IncentiveService incentiveService,
        DonationRepositoryService donationRepositoryService,
        DonationExtraDataRepositoryService donationExtraDataRepositoryService, EntityManager entityManager,
        EventSchedulerService eventSchedulerService, AbstractTwitterService twitterService,
        SelectionService selectionService, @Lazy OengusWebhookService webhookService
    ) {
        this.submissionService = submissionService;
        this.marathonRepositoryService = marathonRepositoryService;
        this.scheduleService = scheduleService;
        this.incentiveService = incentiveService;
        this.donationRepositoryService = donationRepositoryService;
        this.donationExtraDataRepositoryService = donationExtraDataRepositoryService;
        this.entityManager = entityManager;
        this.eventSchedulerService = eventSchedulerService;
        this.twitterService = twitterService;
        this.selectionService = selectionService;
        this.webhookService = webhookService;
    }

    @PostConstruct
    public void initScheduledEvents() {
        final List<Marathon> marathonsWithScheduledSubmissions =
            this.marathonRepositoryService.findFutureMarathonsWithScheduledSubmissions();
        final List<Marathon> marathonsWithScheduleDone =
            this.marathonRepositoryService.findFutureMarathonsWithScheduleDone();
        marathonsWithScheduledSubmissions.forEach(this.eventSchedulerService::scheduleSubmissions);
        marathonsWithScheduleDone.forEach(this.eventSchedulerService::scheduleMarathonStartAlert);
    }

    public Marathon getById(String id) throws NotFoundException {
        return this.marathonRepositoryService.findById(id);
    }

    public String getNameForCode(String code) {
        return this.marathonRepositoryService.getNameById(code);
    }

    @Transactional
    public Marathon create(final Marathon marathon, final User creator) {
        marathon.setCreator(creator);
        marathon.setDefaultSetupTime(Duration.ofMinutes(10));
        marathon.setStartDate(marathon.getStartDate().withSecond(0));
        marathon.setEndDate(marathon.getEndDate().withSecond(0));
        return this.marathonRepositoryService.save(marathon);
    }

    public boolean exists(final String name) {
        return this.marathonRepositoryService.existsById(name);
    }

    @Transactional
    public MarathonDto findOne(final String id) throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(id);
        final MarathonDto marathonDto = new MarathonDto();
        BeanHelper.copyProperties(marathon, marathonDto);
        if (marathon.isHasDonations()) {
            marathonDto.setDonationsTotal(this.donationRepositoryService.findTotalAmountByMarathon(id));
        }
        if (PrincipalHelper.getCurrentUser() != null) {
            marathonDto.setHasSubmitted(
                this.submissionService.userHasSubmitted(marathon, PrincipalHelper.getCurrentUser()));
        }
        return marathonDto;
    }

    @Transactional
    public void update(final String id, final Marathon patch) throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(id);
        Hibernate.initialize(marathon.getQuestions());
        this.entityManager.detach(marathon);

        // final boolean openedSubmissions = !marathon.isSubmitsOpen() && patch.isSubmitsOpen();
        final boolean markedSelectionDone = !marathon.isSelectionDone() && patch.isSelectionDone();

        BeanHelper.copyProperties(patch, marathon, "creator");

        marathon.setStartDate(marathon.getStartDate().withSecond(0));
        marathon.setEndDate(marathon.getEndDate().withSecond(0));

        if (markedSelectionDone) {
            // this.twitterService.sendSelectionDoneTweet(marathon);
            // send accepted submissions
            if (marathon.isAnnounceAcceptedSubmissions() && marathon.hasWebhook()) {
                try {
                    this.webhookService.sendSelectionDoneEvent(
                        marathon.getWebhook(),
                        this.selectionService.findByMarathon(marathon)
                    );
                } catch (IOException e) {
                    LoggerFactory.getLogger(MarathonService.class).error("Sending selection done event failed", e);
                }
            }
        }

        if (marathon.isScheduleDone()) {
            final Schedule schedule = this.scheduleService.findByMarathon(marathon.getId());
            this.scheduleService.computeEndDate(marathon, schedule);
            this.selectionService.rejectTodos(marathon);
            marathon.setSelectionDone(true);
            marathon.setCanEditSubmissions(false);

            // this.twitterService.sendScheduleDoneTweet(marathon);
        }

        if (marathon.isSubmitsOpen()) {
            marathon.setCanEditSubmissions(true);
        }

        /*if (openedSubmissions) {
            this.twitterService.sendSubmissionsOpenTweet(marathon);
        }*/

        if (marathon.getSubmissionsStartDate() != null && marathon.getSubmissionsEndDate() != null) {
            marathon.setSubmissionsStartDate(marathon.getSubmissionsStartDate().withSecond(0));
            marathon.setSubmissionsEndDate(marathon.getSubmissionsEndDate().withSecond(0));
            this.eventSchedulerService.scheduleSubmissions(marathon);
        } else {
            this.eventSchedulerService.unscheduleSubmissions(marathon);
        }

        marathon.getQuestions().forEach(question -> question.setMarathon(marathon));
        this.marathonRepositoryService.update(marathon);
    }

    @Transactional
    public void delete(final String id) throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(id);
        this.eventSchedulerService.unscheduleSubmissions(marathon);
        this.incentiveService.deleteByMarathon(id);
        this.scheduleService.deleteByMarathon(id);
        this.submissionService.deleteByMarathon(marathon);
        this.donationExtraDataRepositoryService.deleteByMarathon(marathon);
        this.twitterService.deleteAudit(marathon);
        this.marathonRepositoryService.delete(marathon);
    }

    private List<MarathonBasicInfoDto> findNext() {
        final List<Marathon> marathons = this.marathonRepositoryService.findNext();
        return this.transform(marathons);
    }

    private List<MarathonBasicInfoDto> findSubmitsOpen() {
        final List<Marathon> marathons = this.marathonRepositoryService.findBySubmitsOpenTrue();
        return this.transform(marathons);
    }

    private List<MarathonBasicInfoDto> findLive() {
        final List<Marathon> marathons = this.marathonRepositoryService.findLive();
        return this.transform(marathons);
    }

    public List<MarathonBasicInfoDto> findActiveMarathonsIModerate(final User user) {
        final List<Marathon> marathons = this.marathonRepositoryService.findActiveMarathonsByCreatorOrModerator(user);
        return this.transform(marathons);
    }

    public List<MarathonBasicInfoDto> findAllMarathonsIModerate(final User user) {
        final List<Marathon> marathons = this.marathonRepositoryService.findAllMarathonsByCreatorOrModerator(user);
        return this.transform(marathons);
    }

    public Map<String, List<MarathonBasicInfoDto>> findMarathons() {
        final Map<String, List<MarathonBasicInfoDto>> marathons = new HashMap<>();
        marathons.put("next", this.findNext());
        marathons.put("open", this.findSubmitsOpen());
        marathons.put("live", this.findLive());
        final User user = PrincipalHelper.getCurrentUser();
        if (user != null) {
            marathons.put("moderated", this.findActiveMarathonsIModerate(user));
        }
        return marathons;
    }

    public List<MarathonBasicInfoDto> findMarathonsForDates(final ZonedDateTime start, final ZonedDateTime end,
                                                            final String zoneId) {
        final List<MarathonBasicInfoDto> dtos = new ArrayList<>();
        final List<Marathon> marathons =
            this.marathonRepositoryService.findBetween(start.withZoneSameInstant(ZoneId.of(zoneId)),
                end.withZoneSameInstant(ZoneId.of(zoneId)).plusDays(1L));
        marathons.forEach(m -> {
            final MarathonBasicInfoDto dto = new MarathonBasicInfoDto();
            BeanUtils.copyProperties(m, dto);
            dtos.add(dto);
        });
        return dtos;
    }

    private List<MarathonBasicInfoDto> transform(final List<Marathon> marathons) {
        final List<MarathonBasicInfoDto> dtos = new ArrayList<>();
        marathons.forEach(m -> {
            final MarathonBasicInfoDto dto = new MarathonBasicInfoDto();
            BeanUtils.copyProperties(m, dto);
            dto.setPrivate(m.getIsPrivate());
            dtos.add(dto);
        });
        return dtos;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void clearDonationExtraData() {
        final List<Marathon> marathons =
            this.marathonRepositoryService.findByClearedFalseAndEndDateBefore(ZonedDateTime.now().minusMonths(1L));
        marathons.forEach(marathon -> {
            this.donationExtraDataRepositoryService.deleteByMarathon(marathon);
            this.marathonRepositoryService.clearMarathon(marathon);
        });
    }
}
