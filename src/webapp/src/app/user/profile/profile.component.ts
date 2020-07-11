import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserProfile } from '../../../model/user-profile';
import { faTwitch, faTwitter } from '@fortawesome/free-brands-svg-icons';
import { faAngleDown, faTrophy } from '@fortawesome/free-solid-svg-icons';
import { Game } from '../../../model/game';
import { DurationService } from '../../../services/duration.service';
import moment from 'moment';
import { UserProfileHistory } from '../../../model/user-profile-history';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  public user: UserProfile;
  public localStorage = localStorage;

  public faTwitch = faTwitch;
  public faTwitter = faTwitter;
  public faTrophy = faTrophy;
  public faAngleDown = faAngleDown;
  public moment = moment;

  public statusFilter = [
    {
      title: 'VALIDATED',
      selected: true
    },
    {
      title: 'BONUS',
      selected: true
    },
    {
      title: 'BACKUP',
      selected: true
    },
    {
      title: 'REJECTED',
      selected: true
    },
    {
      title: 'TODO',
      selected: true
    }
  ];

  public dateFilter = [
    {
      title: 'FUTURE',
      selected: true
    },
    {
      title: 'PAST',
      selected: true
    }
  ];

  constructor(private route: ActivatedRoute) {
    this.user = this.route.snapshot.data.user;
    this.user.history.forEach(marathon => {
      marathon.games.forEach(game => {
        game.categories.forEach(category => {
          category.visible = true;
          category.estimateHuman = DurationService.toHuman(category.estimate);
        });
        game.visible = true;
        game.status = this.getTopStatus(game.categories);
      });
      marathon.visible = true;
      marathon.status = this.getTopStatus(marathon.games);
    });
  }

  ngOnInit() {
  }

  getTopStatus(array: any[]) {
    let status = 'TODO';
    array.forEach(o => {
      switch (o.status.toUpperCase()) {
        case 'VALIDATED':
          status = 'VALIDATED';
          break;
        case 'BONUS':
          if (status !== 'VALIDATED') {
            status = 'BONUS';
          }
          break;
        case 'BACKUP':
          if (status !== 'VALIDATED' && status !== 'BONUS') {
            status = 'BACKUP';
          }
          break;
        case 'REJECTED':
          if (status !== 'VALIDATED' && status !== 'BONUS' && status !== 'BACKUP') {
            status = 'REJECTED';
          }
          break;
        default:
          break;
      }
    });
    return status.toLowerCase();
  }

  public filter() {
    const statuses = this.statusFilter.filter(entry => entry.selected).map(entry => entry.title);
    this.user.history.forEach(marathon => {
      marathon.games.forEach(game => {
        game.categories.forEach(category => {
          category.visible = statuses.includes(category.status);
        });
        game.visible = !!game.categories.find(category => category.visible);
      });
      marathon.visible = !!marathon.games.find(game => game.visible) && this.filterTemporality(marathon);
    });
  }

  private filterTemporality(marathon: UserProfileHistory) {
    const temporalities = this.dateFilter.filter(entry => entry.selected).map(entry => entry.title);
    if (temporalities.length === this.dateFilter.length) {
      return true;
    } else if (temporalities.length === 0) {
      return false;
    } else if (temporalities.includes('PAST')) {
      return this.moment(marathon.marathonStartDate).isBefore(this.moment());
    } else if (temporalities.includes('FUTURE')) {
      return this.moment(marathon.marathonStartDate).isAfter(this.moment());
    }
  }

  public visibleCategories(game: Game) {
    return game.categories.filter(c => c.visible).length;
  }

  public visibleGames(marathon: UserProfileHistory) {
    return marathon.games.map(this.visibleCategories).reduce((previousValue, currentValue) => previousValue + currentValue);
  }

  firstDisplayed(list: any[]) {
    let i = 0;
    for (i; i < list.length; i++) {
      if (list[i].visible) {
        break;
      }
    }
    return i;
  }

}
