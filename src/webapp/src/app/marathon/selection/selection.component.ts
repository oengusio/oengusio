import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Game } from '../../../model/game';
import { faCalendarTimes, faCalendarWeek, faFilm } from '@fortawesome/free-solid-svg-icons';
import { ActivatedRoute } from '@angular/router';
import { SelectionService } from '../../../services/selection.service';
import { MarathonService } from '../../../services/marathon.service';
import { DurationService } from '../../../services/duration.service';
import moment from 'moment-timezone';
import { Selection } from '../../../model/selection';
import * as _ from 'lodash';
import { Availability } from '../../../model/availability';
import * as vis from 'vis-timeline';
import { SubmissionService } from '../../../services/submission.service';

@Component({
  selector: 'app-selection',
  templateUrl: './selection.component.html',
  styleUrls: ['./selection.component.scss']
})
export class SelectionComponent implements OnInit {

  public games: Game[];
  public selection: any;
  public loading = false;

  public faFilm = faFilm;
  public faCalendarWeek = faCalendarWeek;
  public faCalendarTimes = faCalendarTimes;

  @ViewChild('timeline', {static: false}) timeline: ElementRef;
  public availabilitiesGroups: any;
  public availabilitiesItems: any;

  public availabilitiesSelected = [];

  private timezone = moment.tz.guess();

  constructor(private route: ActivatedRoute,
              private selectionService: SelectionService,
              private submissionService: SubmissionService,
              private marathonService: MarathonService) {
    this.availabilitiesGroups = new vis.DataSet([]);
    this.availabilitiesItems = new vis.DataSet([]);
    this.games = this.route.snapshot.data.submissions;
    this.selection = this.route.snapshot.data.selection;
    this.games.forEach(game => {
      game.categories.forEach(category => {
        category.estimateHuman = DurationService.toHuman(category.estimate);
        if (!Object.keys(this.selection).includes(category.id.toString())) {
          const selection = new Selection();
          selection.status = 'TODO';
          selection.categoryId = category.id;
          this.selection[category.id] = selection;
        }
      });
    });
  }

  ngOnInit() {
    const timeline = new vis.Timeline(document.getElementById('timeline'),
      this.availabilitiesItems,
      this.availabilitiesGroups,
      {
        min: moment.tz(this.marathonService.marathon.startDate, this.timezone).subtract(1, 'hours'),
        max: moment.tz(this.marathonService.marathon.endDate, this.timezone).add(1, 'hours')
      });
  }

  getSelectColor(value: String) {
    switch (value) {
      case 'TODO':
        return 'is-warning';
      case 'REJECTED':
        return 'is-danger';
      case 'BONUS':
        return 'is-info';
      case 'BACKUP':
        return 'is-primary';
      case 'VALIDATED':
        return 'is-success';
    }
  }

  submit() {
    this.loading = true;
    this.selectionService.save(this.marathonService.marathon.id, Object.values(this.selection)).add(() => {
      this.loading = false;
    });
  }

  getNumberOfRuns() {
    return Object.keys(this.selection).length;
  }

  getNumberOfRunners() {
    const runners = [];
    this.games.forEach(game => {
      if (!runners.includes(game.user.id)) {
        runners.push(game.user.id);
      }
    });
    return runners.length;
  }

  getTotalTime() {
    const duration = moment.duration(0);
    this.games.forEach(game => {
      game.categories.forEach(category => {
        duration.add(category.estimate);
      });
    });
    return DurationService.toHuman(duration.toISOString());
  }

  getAverageTime() {
    const duration = moment.duration(0);
    this.games.forEach(game => {
      game.categories.forEach(category => {
        duration.add(category.estimate);
      });
    });
    const numberOfRuns = this.getNumberOfRuns();
    if (numberOfRuns === 0) {
      return '0:00:00';
    }
    const averageDuration = moment.duration(duration.asMilliseconds() / numberOfRuns);
    return DurationService.toHuman(averageDuration.toISOString());
  }

  getMarathonLength() {
    const end = moment(this.marathonService.marathon.endDate).tz(this.timezone).seconds(0);
    const start = moment(this.marathonService.marathon.startDate).tz(this.timezone).seconds(0);
    const diff = moment.duration(end.diff(start));
    return DurationService.toHuman(diff.toISOString());
  }

  getDefaultSetupTime() {
    return DurationService.toHuman(this.marathonService.marathon.defaultSetupTime);
  }

  getValidatedRunsTime() {
    const duration = moment.duration(0);
    this.games.forEach(game => {
      game.categories.forEach(category => {
        if (this.selection[category.id].status === 'VALIDATED') {
          duration.add(category.estimate);
          duration.add(moment.duration(this.marathonService.marathon.defaultSetupTime));
        }
      });
    });
    if (duration.asMilliseconds() > 0) {
      duration.subtract(moment.duration(this.marathonService.marathon.defaultSetupTime));
    }
    return DurationService.toHuman(duration.toISOString());
  }

  canPublish() {
    return Object.values(this.selection).filter((value: Selection) => value.status === 'TODO').length === 0;
  }

  publish() {
    this.loading = true;
    this.selectionService.save(this.marathonService.marathon.id, Object.values(this.selection)).add(() => {
      const marathon = _.cloneDeep(this.marathonService.marathon);
      marathon.selectionDone = true;
      marathon.submitsOpen = false;
      this.marathonService.update(marathon, false).add(() => {
        this.loading = false;
      });
    });
  }

  getAvailabilitiesForRunner(userId: number) {
    this.submissionService.availabilitiesForUser(this.marathonService.marathon.id, userId).subscribe(response => {
      for (const [key, value] of Object.entries(response)) {
        const availabilityArray = <Availability[]>value;
        this.availabilitiesSelected.push(key);
        this.availabilitiesGroups.add({
          id: key,
          content: localStorage.getItem('language') === 'ja' ?
            availabilityArray[0].usernameJapanese : availabilityArray[0].username
        });
        availabilityArray.forEach((availability, index) => {
          this.availabilitiesItems.add({
            id: key + index,
            group: key,
            start: availability.from,
            end: availability.to,
            content: ''
          });
        });
      }
    });
  }

  removeAvailabilitiesForRunner(username: string) {
    this.availabilitiesGroups.remove(username);
    this.availabilitiesItems.remove(this.availabilitiesItems.getIds({filter: (item) => item.group === username}));
    this.availabilitiesSelected.splice(this.availabilitiesSelected.findIndex(name => name === username), 1);
  }

  clearAvailabilities() {
    this.availabilitiesSelected.forEach(username => {
      this.availabilitiesGroups.remove(username);
      this.availabilitiesItems.remove(this.availabilitiesItems.getIds({filter: (item) => item.group === username}));
    });
    this.availabilitiesSelected = [];
  }
}
