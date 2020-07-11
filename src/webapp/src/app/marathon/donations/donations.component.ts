import { Component, OnInit } from '@angular/core';
import { Donation } from '../../../model/donation';
import { ActivatedRoute } from '@angular/router';
import { MarathonService } from '../../../services/marathon.service';
import { DonationService } from '../../../services/donation.service';
import { Page } from '../../../model/page';
import moment from 'moment';
import { NwbPageEvent } from '@wizishop/ng-wizi-bulma';
import { DonationStats } from '../../../model/donation-stats';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-donations',
  templateUrl: './donations.component.html',
  styleUrls: ['./donations.component.scss']
})
export class DonationsComponent implements OnInit {

  public donations: Page<Donation>;
  public stats: DonationStats;
  public moment = moment;
  public pageSizeOptions = [10, 25, 50, 100];

  constructor(private route: ActivatedRoute,
              public marathonService: MarathonService,
              private donationService: DonationService,
              public userService: UserService) {
    this.donations = this.route.snapshot.data.donations;
    this.stats = this.route.snapshot.data.stats;
    this.marathonService.marathon.donationsTotal = this.stats.total;
  }

  ngOnInit() {
  }

  pageChange(event: NwbPageEvent) {
    this.donationService.find(this.marathonService.marathon.id, event.pageIndex, event.pageSize).subscribe(response => {
      this.donations = response;
    });
  }

  exportToCsv() {
    this.donationService.exportAllForMarathon(this.marathonService.marathon.id);
  }

}
