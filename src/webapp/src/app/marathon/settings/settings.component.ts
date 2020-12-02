import { Component, OnInit } from '@angular/core';
import { Marathon } from '../../../model/marathon';
import { MarathonService } from '../../../services/marathon.service';
import isoLang from '../../../assets/languages.json';
import countries from '../../../assets/countries.json';
import { User } from '../../../model/user';
import { UserService } from '../../../services/user.service';
import * as _ from 'lodash';
import { DurationService } from '../../../services/duration.service';
import moment from 'moment';
import { environment } from '../../../environments/environment';
import { faBars, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons';
import { Question } from '../../../model/question';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { DonationService } from '../../../services/donation.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {

  public marathon: Marathon;
  public languages = (<any>isoLang);
  public countries = countries;
  public env = environment;
  public loading = false;
  public now: Date;

  public data = [];
  public active = 'general';

  public deleteConfirm = false;
  public deleteShortname: string;

  public faTimes = faTimes;
  public faPlus = faPlus;
  public faBars = faBars;

  public submissionsQuestions: Question[];
  public donationsQuestions: Question[];

  public loadDonationWebhookCheck: boolean;
  public isWebhookOnline = true;

  constructor(public marathonService: MarathonService,
              private donationService: DonationService,
              public userService: UserService) {
    this.now = new Date();
    this.now.setSeconds(0);
  }

  ngOnInit() {
    this.marathon = _.cloneDeep(this.marathonService.marathon);
    this.marathon.defaultSetupTimeHuman = DurationService.toHuman(this.marathon.defaultSetupTime);
    this.submissionsQuestions = this.marathon.questions.filter(q => q.questionType === 'SUBMISSION');
    this.donationsQuestions = this.marathon.questions.filter(q => q.questionType === 'DONATION');
  }

  checkWebhook(text: any) {
    if (text) {
      this.loadDonationWebhookCheck = true;
      this.donationService.isWebhookOnline(this.marathonService.marathon.id, text)
        .subscribe(() => this.isWebhookOnline = true, () => this.isWebhookOnline = false)
        .add(() => this.loadDonationWebhookCheck = false);
    } else {
      this.isWebhookOnline = true;
    }
  }

  onSelectMod(item: User) {
    if (this.marathon.moderators.findIndex(user => user.id === item.id) < 0
      && this.marathon.creator.id !== item.id) {
      this.marathon.moderators.push(item);
    }
  }

  onSearchMod(val: string) {
    if (!val || val.length < 3) {
      return;
    }
    this.userService.search(val).subscribe(response => {
      this.data = response;
    });
  }

  removeModerator(index: number) {
    this.marathon.moderators.splice(index, 1);
  }

  submit() {
    this.loading = true;
    this.marathon.defaultSetupTime = moment.duration(this.marathon.defaultSetupTimeHuman).toISOString();
    this.marathon.questions = [];
    this.marathon.questions = this.marathon.questions.concat(this.submissionsQuestions);
    this.marathon.questions = this.marathon.questions.concat(this.donationsQuestions);
    this.marathonService.update(this.marathon).add(() => {
      this.loading = false;
    });
  }

  addQuestion(questionType: string) {
    const question = new Question(questionType);
    if (questionType === 'SUBMISSION') {
      question.position = this.submissionsQuestions.length;
      this.submissionsQuestions.push(question);
    }
    if (questionType === 'DONATION') {
      question.position = this.donationsQuestions.length;
      this.donationsQuestions.push(question);
    }
    this.computeQuestionsPositions();
  }

  questionTypeChange(questionType: string, index: number, fieldType: string) {
    if (questionType === 'SUBMISSION') {
      if (this.submissionsQuestions[index].fieldType === 'FREETEXT') {
        this.submissionsQuestions[index].required = false;
      }
    }
    if (questionType === 'DONATION') {
      if (this.donationsQuestions[index].fieldType === 'FREETEXT') {
        this.donationsQuestions[index].required = false;
      }
    }
  }

  computeQuestionsPositions() {
    for (let i = 0; i < this.donationsQuestions.length; i++) {
      this.donationsQuestions[i].position = i;
    }
    for (let j = 0; j < this.submissionsQuestions.length; j++) {
      this.submissionsQuestions[j].position = j;
    }
  }

  removeQuestion(questionType: string, i: number) {
    if (questionType === 'SUBMISSION') {
      this.submissionsQuestions.splice(i, 1);
    }
    if (questionType === 'DONATION') {
      this.donationsQuestions.splice(i, 1);
    }
    this.computeQuestionsPositions();
  }

  addOption(questionType: string, i: number) {
    if (questionType === 'SUBMISSION') {
      this.submissionsQuestions[i].options.push('');
    }
    if (questionType === 'DONATION') {
      this.donationsQuestions[i].options.push('');
    }
  }

  removeOption(questionType: string, i: number, j: number) {
    if (questionType === 'SUBMISSION') {
      this.submissionsQuestions[i].options.splice(j, 1);
    }
    if (questionType === 'DONATION') {
      this.donationsQuestions[i].options.splice(j, 1);
    }
  }

  trackByIdx(index: number, obj: any): any {
    return index;
  }

  drop(event: CdkDragDrop<Question[]>) {
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    this.computeQuestionsPositions();
  }
}
