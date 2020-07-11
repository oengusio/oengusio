import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Incentive } from '../../../model/incentive';
import { MarathonService } from '../../../services/marathon.service';

@Component({
  selector: 'app-incentive',
  templateUrl: './incentive.component.html',
  styleUrls: ['./incentive.component.scss']
})
export class IncentiveComponent implements OnInit {

  public incentives: Incentive[];

  constructor(private route: ActivatedRoute,
              public marathonService: MarathonService) {
    this.incentives = this.route.snapshot.data.incentives;
  }

  ngOnInit() {
  }

}
