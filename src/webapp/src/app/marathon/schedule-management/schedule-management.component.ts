import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  faBars,
  faCalendarTimes,
  faCalendarWeek,
  faChevronLeft,
  faChevronRight,
  faEdit,
  faTimes
} from '@fortawesome/free-solid-svg-icons';
import { DurationService } from '../../../services/duration.service';
import { ScheduleLine } from '../../../model/schedule-line';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { MarathonService } from '../../../services/marathon.service';
import moment from 'moment-timezone';
import { ScheduleService } from '../../../services/schedule.service';
import { Schedule } from '../../../model/schedule';
import { NwbEditInPlaceConfig, ReturnedData } from '@wizishop/ng-wizi-bulma';
import { Observable } from 'rxjs';
import { User } from '../../../model/user';
import { UserService } from '../../../services/user.service';
import { Availability } from '../../../model/availability';
import * as vis from 'vis-timeline';
import { SubmissionService } from '../../../services/submission.service';

@Component({
  selector: 'app-schedule-management',
  templateUrl: './schedule-management.component.html',
  styleUrls: ['./schedule-management.component.scss']
})
export class ScheduleManagementComponent implements OnInit {

  public scheduleTodo: ScheduleLine[];
  public schedule: Schedule;
  public loading = false;

  public userSearch = {};
  public editMode = {};

  public faBars = faBars;
  public faEdit = faEdit;
  public faTimes = faTimes;
  public faCalendarWeek = faCalendarWeek;
  public faCalendarTimes = faCalendarTimes;
  public faChevronRight = faChevronRight;
  public faChevronLeft = faChevronLeft;

  public moment = moment;

  private timezone = moment.tz.guess();

  private timeline: any;
  private timebar: string;
  private availabilitiesSelectedItems = [];
  public availabilitiesGroups: any;
  public availabilitiesItems: any;
  public availabilitiesSelected = [];

  public durationEditConfig: NwbEditInPlaceConfig = {
    handler: (value) => {
      return new Observable<boolean | ReturnedData>(subscriber => {
        subscriber.next(moment.duration(value).asSeconds() > 0);
        subscriber.complete();
      });
    }
  };

  public setupDurationEditConfig: NwbEditInPlaceConfig = {
    handler: (value) => {
      return new Observable<boolean | ReturnedData>(subscriber => {
        subscriber.next(moment.duration(value).asSeconds() >= 0);
        subscriber.complete();
      });
    }
  };

  constructor(private route: ActivatedRoute,
              public marathonService: MarathonService,
              private scheduleService: ScheduleService,
              private submissionService: SubmissionService,
              private userService: UserService) {
    this.availabilitiesGroups = new vis.DataSet([]);
    this.availabilitiesItems = new vis.DataSet([]);
    const availabilities = this.route.snapshot.data.availabilities;
    for (const [key, value] of Object.entries(availabilities)) {
      this.availabilitiesGroups.add({
        id: key,
        content: key
      });
      const availabilityArray = <Availability[]>value;
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

    const selection = this.route.snapshot.data.selection;
    const games = this.route.snapshot.data.submissions
      .filter(game =>
        game.categories
          .filter(category =>
            Object.keys(selection).includes(category.id.toString()))
          .length > 0
      );
    this.initSchedule(this.route.snapshot.data.schedule);
    this.scheduleTodo = [];
    games.forEach(game => {
      game.categories
        .filter(category => Object.keys(selection).includes(category.id.toString()))
        .filter(category => !this.schedule.lines.map(line => line.categoryId).includes(category.id))
        .forEach(category => {
          const scheduleLine = new ScheduleLine();
          scheduleLine.categoryId = category.id;
          scheduleLine.categoryName = category.name;
          scheduleLine.console = game.console;
          scheduleLine.estimate = category.estimate;
          scheduleLine.estimateHuman = DurationService.toHuman(category.estimate);
          scheduleLine.gameName = game.name;
          scheduleLine.ratio = game.ratio;
          scheduleLine.runners = [game.user];
          category.opponentDtos.forEach(opponent => scheduleLine.runners.push(opponent.user));
          scheduleLine.setupTime = this.marathonService.marathon.defaultSetupTime;
          scheduleLine.setupTimeHuman = DurationService.toHuman(this.marathonService.marathon.defaultSetupTime);
          scheduleLine.setupBlock = false;
          scheduleLine.type = scheduleLine.runners.length > 1 ? 'RACE' : 'SINGLE';
          this.scheduleTodo.push(scheduleLine);
        });
    });
  }

  initSchedule(schedule: Schedule) {
    if (!!schedule) {
      this.schedule = schedule;
    } else {
      this.schedule = new Schedule();
    }
    this.schedule.lines.forEach(line => {
      line.useSetupBlockText = !!line.setupBlockText;
      line.setupTimeHuman = DurationService.toHuman(line.setupTime);
      line.estimateHuman = DurationService.toHuman(line.estimate);
    });
  }

  ngOnInit() {
    this.timeline = new vis.Timeline(document.getElementById('timeline'),
      this.availabilitiesItems,
      this.availabilitiesGroups,
      {
        min: moment.tz(this.marathonService.marathon.startDate, this.timezone).subtract(1, 'hours'),
        max: moment.tz(this.marathonService.marathon.endDate, this.timezone).add(1, 'hours')
      });
    this.timebar = this.timeline.addCustomTime(this.marathonService.marathon.startDate);
    this.computeSchedule();
  }

  computeSchedule() {
    if (!this.schedule.lines.length) {
      return;
    }
    this.schedule.lines[0].date = this.marathonService.marathon.startDate;
    this.schedule.lines[0].position = 0;
    for (let i = 1; i < this.schedule.lines.length; i++) {
      this.schedule.lines[i].date = moment.tz(this.schedule.lines[i - 1].date, this.timezone)
        .add(moment.duration(this.schedule.lines[i - 1].estimate))
        .add(moment.duration(this.schedule.lines[i - 1].setupTime))
        .toDate();
      this.schedule.lines[i].position = i;
    }
    const lastElement = this.schedule.lines[this.schedule.lines.length - 1];
    if (this.timeline) {
      this.timeline.setCustomTime(moment.tz(lastElement.date, this.timezone)
        .add(moment.duration(lastElement.estimate))
        .add(moment.duration(lastElement.setupTime))
        .toDate(), this.timebar);
    }
  }

  drop(event: CdkDragDrop<ScheduleLine[]>) {
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    this.computeSchedule();
  }

  estimateChange(line: ScheduleLine, event: any) {
    line.estimate = moment.duration(event).toISOString();
    this.computeSchedule();
  }

  setupChange(line: ScheduleLine, event: any) {
    line.setupTime = moment.duration(event).toISOString();
    this.computeSchedule();
  }

  submit() {
    this.loading = true;
    this.schedule.lines.forEach(line => {
      if (!line.useSetupBlockText) {
        line.setupBlockText = null;
      }
    });
    this.scheduleService.save(this.marathonService.marathon.id, this.schedule).add(() => {
      this.loading = false;
      this.scheduleService.getAllForMarathon(this.marathonService.marathon.id).subscribe(response => {
        this.initSchedule(response);
        this.computeSchedule();
        this.marathonService.find(this.marathonService.marathon.id).subscribe(marathon => this.marathonService.marathon = marathon);
      });
    });
  }

  publish() {
    this.submit();
    this.marathonService.update({...this.marathonService.marathon, scheduleDone: true}, false);
  }

  onSelectUser(item: User, line: ScheduleLine) {
    if (line.runners.findIndex(user => user.id === item.id) < 0) {
      line.runners.push(item);
      if (line.runners.length === 2) {
        line.type = 'RACE';
      }
      this.getAvailabilitiesForRunner(item.id);
    }
  }

  onSearchUser(val: string, categoryId: number) {
    if (!val || val.length < 3) {
      return;
    }
    this.userService.search(val).subscribe(response => {
      this.userSearch[categoryId] = response;
    });
  }

  removeUser(index: number, line: ScheduleLine) {
    line.runners.splice(index, 1);
    if (line.runners.length === 1) {
      line.type = 'SINGLE';
    }
  }

  editInProgress() {
    return Object.values(this.editMode).some(Boolean);
  }

  addLine(isSetup: boolean) {
    const setupBlock = new ScheduleLine();
    setupBlock.setupBlock = isSetup;
    setupBlock.customRun = !isSetup;
    setupBlock.type = 'OTHER';
    setupBlock.position = this.schedule.lines.length;
    setupBlock.setupTime = this.marathonService.marathon.defaultSetupTime;
    setupBlock.setupTimeHuman = DurationService.toHuman(setupBlock.setupTime);
    setupBlock.estimate = moment.duration(0, 'seconds').toISOString();

    this.schedule.lines.push(setupBlock);
    this.computeSchedule();
  }

  removeLine(index: number) {
    this.schedule.lines.splice(index, 1);
    this.computeSchedule();
  }

  moveToSchedule(index: number) {
    this.schedule.lines.push(this.scheduleTodo[index]);
    this.scheduleTodo.splice(index, 1);
    this.computeSchedule();
  }

  moveToTodo(index: number) {
    this.scheduleTodo.push(this.schedule.lines[index]);
    this.schedule.lines.splice(index, 1);
    this.computeSchedule();
  }

  selectAvailabilities(username: string) {
    this.availabilitiesSelected.push(username);
    this.availabilitiesSelectedItems = [...new Set([...this.availabilitiesSelectedItems,
      ...this.availabilitiesItems.getIds({filter: (item) => item.group === username})])];
    this.timeline.setSelection(this.availabilitiesSelectedItems);
  }

  unselectAvailabilities(username: string) {
    this.availabilitiesSelected.splice(this.availabilitiesSelected.findIndex(name => name === username), 1);
    const toRemove = this.availabilitiesItems.getIds({filter: (item) => item.group === username});
    this.availabilitiesSelectedItems = this.availabilitiesSelectedItems.filter(el => !toRemove.includes(el));
    this.timeline.setSelection(this.availabilitiesSelectedItems);
  }

  clearAvailabilities() {
    const toRemove = this.availabilitiesItems.getIds({filter: (item) => this.availabilitiesSelected.includes(item.group)});
    this.availabilitiesSelected = [];
    this.availabilitiesSelectedItems = this.availabilitiesSelectedItems.filter(el => !toRemove.includes(el));
    this.timeline.setSelection(this.availabilitiesSelectedItems);
  }

  getAvailabilitiesForRunner(userId: number) {
    this.submissionService.availabilitiesForUser(this.marathonService.marathon.id, userId).subscribe(response => {
      for (const [key, value] of Object.entries(response)) {
        const availabilityArray = <Availability[]>value;
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

  matchesAvailabilities(line: ScheduleLine) {
    return line.runners.every(user => this.route.snapshot.data.availabilities[user.username] &&
      this.route.snapshot.data.availabilities[user.username].some(availability => {
        const startDateAvail = moment.tz(availability.from, this.timezone);
        const endDateAvail = moment.tz(availability.to, this.timezone);
        const startDateRun = moment.tz(line.date, this.timezone);
        const endDateRun = moment.tz(line.date, this.timezone).add(moment.duration(line.estimate));
        return startDateAvail.isSameOrBefore(startDateRun) && endDateAvail.isSameOrAfter(endDateRun);
      }));
  }

}
