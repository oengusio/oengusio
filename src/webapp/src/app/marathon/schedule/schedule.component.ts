import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Schedule } from '../../../model/schedule';
import { DurationService } from '../../../services/duration.service';
import moment from 'moment-timezone';
//import 'moment-timezone/builds/moment-timezone-with-data-10-year-range';
import { MarathonService } from '../../../services/marathon.service';
import { ScheduleService } from '../../../services/schedule.service';
import { faAngleDown } from '@fortawesome/free-solid-svg-icons';
import { ScheduleLine } from '../../../model/schedule-line';
import { Subscription, timer } from 'rxjs';

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent implements OnInit, OnDestroy {

  public schedule: Schedule;
  public moment = moment;

  public timezone = moment.tz.guess();

  public faAngleDown = faAngleDown;
  public exportActive = false;

  public currentIndex: number;
  private scheduleRefresher: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              public marathonService: MarathonService,
              private scheduleService: ScheduleService) {
    if (!this.marathonService.marathon.scheduleDone) {
      this.router.navigate(['../'], {relativeTo: this.route});
    }
    if (this.route.snapshot.data.schedule) {
      this.schedule = this.route.snapshot.data.schedule;
    } else {
      this.schedule = new Schedule();
    }
    this.schedule.lines.forEach(line => {
      line.setupTimeHuman = DurationService.toHuman(line.setupTime);
      line.estimateHuman = DurationService.toHuman(line.estimate);
    });
    this.getCurrentRun();
    const now = new Date();
    const initialDelay = 60 * 1000 - (now.getSeconds() * 1000 + now.getMilliseconds());
    this.scheduleRefresher = timer(initialDelay, 60000).subscribe(() => this.getCurrentRun());
  }

  getCurrentRun() {
    this.schedule.lines.forEach((line, index) => {
      if (this.isCurrentRun(line)) {
        this.currentIndex = index;
      }
    });
  }

  ngOnInit() {
  }

  export(format: string) {
    this.scheduleService.exportAllForMarathon(this.marathonService.marathon.id, format);
  }

  isCurrentRun(line: ScheduleLine) {
    const now = moment.now();
    return moment.tz(line.date, this.timezone).isBefore(now) &&
      moment.tz(line.date, this.timezone)
        .add(moment.duration(line.estimate))
        .add(moment.duration(line.setupTime)).isAfter(now);
  }

  isLive() {
    const now = moment();
    return moment.tz(this.marathonService.marathon.startDate, this.timezone).isBefore(now)
      && moment.tz(this.marathonService.marathon.endDate, this.timezone).isAfter(now);
  }

  ngOnDestroy(): void {
    this.scheduleRefresher.unsubscribe();
  }

}
