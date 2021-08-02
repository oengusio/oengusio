package app.oengus.service;

import app.oengus.entity.model.Incentive;
import app.oengus.entity.model.Marathon;
import app.oengus.service.repository.BidRepositoryService;
import app.oengus.service.repository.DonationIncentiveLinkRepositoryService;
import app.oengus.service.repository.IncentiveRepositoryService;
import app.oengus.service.repository.MarathonRepositoryService;
import javassist.NotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IncentiveService {

    private final IncentiveRepositoryService incentiveRepositoryService;
    private final BidRepositoryService bidRepositoryService;
    private final MarathonRepositoryService marathonRepositoryService;
    private final DonationIncentiveLinkRepositoryService donationIncentiveLinkRepositoryService;

    public IncentiveService(
        IncentiveRepositoryService incentiveRepositoryService,
        BidRepositoryService bidRepositoryService, MarathonRepositoryService marathonRepositoryService,
        DonationIncentiveLinkRepositoryService donationIncentiveLinkRepositoryService
    ) {
        this.incentiveRepositoryService = incentiveRepositoryService;
        this.bidRepositoryService = bidRepositoryService;
        this.marathonRepositoryService = marathonRepositoryService;
        this.donationIncentiveLinkRepositoryService = donationIncentiveLinkRepositoryService;
    }

    public Incentive findById(int incentiveId) throws NotFoundException {
        return this.incentiveRepositoryService.findById(incentiveId).orElseThrow(
            () -> new NotFoundException("Incentive not found")
        );
    }

    public List<Incentive> findByMarathon(final String marathonId, final boolean withLocked,
                                          final boolean withUnapproved) throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(marathonId);
        final List<Incentive> incentives;
        if (withLocked) {
            incentives = this.incentiveRepositoryService.findByMarathon(marathonId);
        } else {
            incentives = this.incentiveRepositoryService.findByMarathonNotLocked(marathonId);
        }

        if (marathon.isHasDonations()) {
            final Map<Integer, BigDecimal> incentivesAmounts = this.incentiveRepositoryService.findAmountsByMarathon(marathonId);
            final Map<Integer, BigDecimal> bidsAmounts = this.bidRepositoryService.findAmountsByMarathon(marathonId);

            incentives.forEach(incentive -> {
                if (incentive.isBidWar()) {
                    if (!withUnapproved && CollectionUtils.isNotEmpty(incentive.getBids())) {
                        incentive.setBids(
                            incentive.getBids()
                                .stream()
                                .filter(bid -> BooleanUtils.isTrue(bid.getApproved()))
                                .collect(Collectors.toList()));
                    }
                    incentive.getBids().forEach(bid -> {
                        bid.setCurrentAmount(bidsAmounts.getOrDefault(bid.getId(), BigDecimal.ZERO));
                    });
                } else {
                    incentive.setCurrentAmount(incentivesAmounts.getOrDefault(incentive.getId(), BigDecimal.ZERO));
                }
            });
        }
        return incentives;
    }

    @Transactional
    public List<Incentive> saveAll(final List<Incentive> incentives, final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        incentives.forEach((incentive) -> {
            if (incentive.getId() == 0) {
                incentive.setId(-1);
            }

            incentive.setMarathon(marathon);

            if (incentive.getBids() != null) {
                incentive.getBids().forEach((bid) -> {
                    if (bid.getId() == 0) {
                        bid.setId(-1);
                    }

                    bid.setIncentive(incentive);
                });
            }
        });

        incentives.forEach(incentive -> {
            if (incentive.isBidWar()) {
                incentive.getBids().forEach(bid -> {
                    if (bid.isToDelete()) {
                        this.donationIncentiveLinkRepositoryService.deleteByBid(bid);
                        this.bidRepositoryService.delete(bid);
                    }
                });
                incentive.setBids(
                    incentive.getBids().stream().filter(bid -> !bid.isToDelete()).collect(Collectors.toList())
                );
            }
        });

        incentives.stream()
            .filter(incentive -> incentive.getId() > 0 && incentive.isToDelete())
            .forEach(i -> {
                if (i.isBidWar()) {
                    i.getBids().forEach(bid -> this.donationIncentiveLinkRepositoryService.deleteByBid(bid));
                }
                this.donationIncentiveLinkRepositoryService.deleteByIncentive(i);
                this.incentiveRepositoryService.delete(i.getId());
            });

        return this.incentiveRepositoryService.saveAll(
            incentives.stream().filter(incentive -> !incentive.isToDelete()).collect(Collectors.toList())
        );
    }

    public void deleteByMarathon(final String marathonId) {
        this.incentiveRepositoryService.delete(marathonId);
    }
}
