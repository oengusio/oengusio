import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Game } from '../../../model/game';
import { faCheck, faFilm, faTimes } from '@fortawesome/free-solid-svg-icons';
import moment from 'moment';
import { MarathonService } from '../../../services/marathon.service';
import { DurationService } from '../../../services/duration.service';
import { Category } from '../../../model/category';
import { UserService } from '../../../services/user.service';
import { Submission } from '../../../model/submission';
import { GameService } from '../../../services/game.service';
import { SubmissionService } from '../../../services/submission.service';
import { CategoryService } from '../../../services/category.service';

@Component({
  selector: 'app-submissions',
  templateUrl: './submissions.component.html',
  styleUrls: ['./submissions.component.scss']
})
export class SubmissionsComponent implements OnInit {

  public games: Game[];
  public answers: Submission[];
  public selection: Map<number, Selection>;

  public runnerFilter = '';
  public gameFilter = '';
  public categoryFilter = '';

  public confirmDeletion = {
    submission: {},
    game: {},
    category: {}
  };

  public active = 'submissions';

  public faFilm = faFilm;
  public faTimes = faTimes;
  public faCheck = faCheck;

  public moment = moment;

  constructor(private route: ActivatedRoute,
              public marathonService: MarathonService,
              public userService: UserService,
              public gameService: GameService,
              private submissionService: SubmissionService,
              private categoryService: CategoryService) {
    this.games = this.route.snapshot.data.submissions;
    this.selection = this.route.snapshot.data.selection;
    this.answers = this.route.snapshot.data.answers;
    this.games.forEach(game => {
      game.visible = true;
      game.categories.forEach(category => {
        category.estimateHuman = DurationService.toHuman(category.estimate);
        category.visible = true;
      });
    });
    this.answers.forEach(submission => {
      submission.answers = submission.answers.filter(answer => answer.question.fieldType !== 'FREETEXT');
    });
  }

  ngOnInit() {
  }

  getGameStatus(game: Game) {
    if (!this.marathonService.marathon.selectionDone) {
      return '';
    }
    let status = 'REJECTED';
    game.categories.filter(c => c.visible).forEach(category => {
      if (!!this.selection[category.id]) {
        switch (this.selection[category.id].status) {
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
          default:
            break;
        }
      }
    });
    return status.toLowerCase();
  }

  getCategoryStatus(category: Category) {
    if (!this.marathonService.marathon.selectionDone || !this.selection[category.id]) {
      return '';
    }
    return this.selection[category.id].status.toLowerCase();
  }

  displaysTabs() {
    return this.marathonService.isAdmin(this.userService.user) &&
      !!this.marathonService.marathon.questions &&
      this.marathonService.marathon.questions.length > 0;
  }

  exportToCsv() {
    this.gameService.exportAllForMarathon(this.marathonService.marathon.id);
  }

  filter() {
    this.games.forEach(game => {
      game.categories.forEach(category => {
        category.visible = !this.categoryFilter || this.selection[category.id].status === this.categoryFilter;
      });
      game.visible = this.filterGame(game);
    });
  }

  private filterGame(game: Game) {
    if (!this.runnerFilter && !this.gameFilter && !this.categoryFilter) {
      return true;
    }
    let visible = true;
    if (!!this.runnerFilter) {
      if (localStorage.getItem('language') === 'ja' && !!game.user.usernameJapanese) {
        visible = visible && game.user.usernameJapanese.toLowerCase().includes(this.runnerFilter.toLowerCase());
      } else {
        visible = visible && game.user.username.toLowerCase().includes(this.runnerFilter.toLowerCase());
      }
    }
    if (!!this.gameFilter) {
      visible = visible && game.name.toLowerCase().includes(this.gameFilter.toLowerCase());
    }
    visible = visible && game.categories.map(c => c.visible).includes(true);
    return visible;
  }

  firstDisplayed(game: Game) {
    let i = 0;
    for (i; i < game.categories.length; i++) {
      if (game.categories[i].visible) {
        break;
      }
    }
    return i;
  }

  visibleCategories(game: Game) {
    return game.categories.filter(c => c.visible).length;
  }

  deleteSubmission(id: number) {
    this.submissionService.delete(this.marathonService.marathon.id, id).add(() => {
      this.games = this.games.filter(game => game.submissionId !== id);
    });
  }

  deleteGame(id: number) {
    this.gameService.delete(this.marathonService.marathon.id, id).add(() => {
      this.games = this.games.filter(game => game.id !== id);
    });
  }

  deleteCategory(gameId: number, id: number) {
    this.categoryService.delete(this.marathonService.marathon.id, id).add(() => {
      const game = this.games.find(g => g.id === gameId);
      game.categories = game.categories.filter(c => c.id !== id);
      if (game.categories.length === 0) {
        this.deleteGame(gameId);
      }
    });
  }
}
