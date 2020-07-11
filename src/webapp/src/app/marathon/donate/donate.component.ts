import { Component, NgZone, OnInit } from '@angular/core';
import { Donation } from '../../../model/donation';
import { DonationService } from '../../../services/donation.service';
import { MarathonService } from '../../../services/marathon.service';
import { UserService } from '../../../services/user.service';
import { IPayPalConfig } from 'ngx-paypal';
import { environment } from '../../../environments/environment';
import { DonationExtraData } from '../../../model/donation-extra-data';
import { Incentive } from '../../../model/incentive';
import { ActivatedRoute, Router } from '@angular/router';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { DonationIncentiveLink } from '../../../model/donation-incentive-link';
import { Bid } from '../../../model/bid';

@Component({
  selector: 'app-donate',
  templateUrl: './donate.component.html',
  styleUrls: ['./donate.component.scss']
})
export class DonateComponent implements OnInit {

  public donation = new Donation();
  public incentives: Incentive[];

  public links: Link[];

  public paypalConfig: IPayPalConfig;
  public loading = false;
  public faTimes = faTimes;

  isBid = (tbd: any): tbd is Bid => (tbd as Bid).incentiveId !== undefined;
  isIncentive = (tbd: any): tbd is Incentive => (tbd as Incentive).scheduleLine !== undefined;

  constructor(private donationService: DonationService,
              public marathonService: MarathonService,
              public userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              private zone: NgZone) {
    this.incentives = this.route.snapshot.data.incentives;
    this.incentives.forEach(incentive => {
      if (incentive.bidWar && incentive.openBid) {
        const bid = new Bid();
        bid.approved = false;
        bid.isNew = true;
        bid.incentiveId = incentive.id;
        incentive.bids.push(bid);
      }
    });
    this.links = [];
    if (!!userService.user) {
      this.donation.nickname = userService.user.username;
    }
    if (this.marathonService.marathon.questions.length > 0) {
      if (!this.donation.answers || this.donation.answers.length === 0) {
        this.donation.answers = [];
        this.marathonService.marathon.questions.forEach(question => {
          if (question.questionType === 'DONATION') {
            const answer = new DonationExtraData();
            answer.question = question;
            if (question.fieldType === 'CHECKBOX') {
              answer.answer = false;
            }
            this.donation.answers.push(answer);
          }
        });
      } else {
        this.donation.answers.forEach(answer => {
          if (answer.question.fieldType === 'CHECKBOX') {
            answer.answer = Boolean(answer.answer);
          }
        });
      }
    }
  }

  ngOnInit(): void {
    this.initConfig();
  }

  addLink() {
    this.links.push(new Link());
  }

  removeLink(index: number) {
    this.links.splice(index, 1);
  }

  getLeftAmount() {
    const total = this.donation.amount ? this.donation.amount : 0;
    return total - this.links.map(link => link.amount).reduce((acc, cur) => acc + cur, 0);
  }

  private initConfig(): void {
    this.paypalConfig = {
      currency: this.marathonService.marathon.donationCurrency,
      clientId: environment.paypalClientId,
      createOrderOnServer: (data) => {
        return new Promise<string>((resolve, reject) => {
          this.links.forEach(link => {
            const donationIncentiveLink = new DonationIncentiveLink();
            donationIncentiveLink.amount = link.amount;
            if (this.isBid(link.incentive)) {
              donationIncentiveLink.bid = link.incentive;
            } else if (this.isIncentive(link.incentive)) {
              donationIncentiveLink.incentive = link.incentive;
            }
            this.donation.donationIncentiveLinks.push(donationIncentiveLink);
          });
          this.donationService.donate(this.marathonService.marathon.id, this.donation).subscribe(response => {
            console.log(response);
            resolve(response.body.id);
          });
        });
      },
      advanced: {
        commit: 'true'
      },
      style: {
        label: 'paypal',
        layout: 'vertical'
      },
      onClientAuthorization: (data) => {
        this.zone.run(() => {
          this.donationService.validate(this.marathonService.marathon.id, data.id).add(() => {
            this.loading = false;
            this.router.navigate(['marathon', this.marathonService.marathon.id, 'donations']);
          });
        });
      },
      onApprove: (data) => {
        this.loading = true;
      },
      onCancel: (data) => {
        this.zone.run(() => {
          this.donationService.cancel(this.marathonService.marathon.id, data.orderID).subscribe(() => {
            this.loading = false;
          });
        });
      }
    };
  }
}

export class Link {
  incentive: any;
  amount = 0;
}
