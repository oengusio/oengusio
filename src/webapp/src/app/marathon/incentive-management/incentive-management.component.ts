import { Component, OnInit } from '@angular/core';
import { IncentiveService } from '../../../services/incentive.service';
import { ActivatedRoute } from '@angular/router';
import { Incentive } from '../../../model/incentive';
import { Schedule } from '../../../model/schedule';
import { MarathonService } from '../../../services/marathon.service';
import { faCheck, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons';
import { Bid } from '../../../model/bid';
import { ScheduleLine } from '../../../model/schedule-line';

@Component({
  selector: 'app-incentive-management',
  templateUrl: './incentive-management.component.html',
  styleUrls: ['./incentive-management.component.scss']
})
export class IncentiveManagementComponent implements OnInit {

  public incentives: Incentive[];
  public schedule: Schedule;
  public faPlus = faPlus;
  public faTimes = faTimes;
  public faCheck = faCheck;

  public loading = false;

  constructor(private route: ActivatedRoute,
              private incentiveService: IncentiveService,
              private marathonService: MarathonService) {
    this.schedule = this.route.snapshot.data.schedule;
    this.incentives = this.route.snapshot.data.incentives;
  }

  ngOnInit() {
  }

  submit() {
    this.loading = true;
    this.incentiveService.saveAll(this.marathonService.marathon.id, this.incentives).add(() => {
      this.incentiveService.getAllForMarathon(this.marathonService.marathon.id).subscribe(response => {
        this.incentives = response;
        this.loading = false;
      });
    });
  }

  addIncentive() {
    this.incentives.push(new Incentive());
  }

  addBid(i: number) {
    this.incentives[i].bids.push(new Bid());
  }

  byId(item1: ScheduleLine, item2: ScheduleLine) {
    if (item1 == null || item2 == null) {
      return false;
    }
    return item1.id === item2.id;
  }

  removeBid(i: number, j: number) {
    this.incentives[i].bids[j].toDelete = true;
  }

  countNotDeletedBids(incentive: Incentive) {
    return incentive.bids.filter(bid => !bid.toDelete).length;
  }
}
