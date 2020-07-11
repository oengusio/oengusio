import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../services/user.service';
import { MarathonService } from '../../services/marathon.service';
import {
  faBook,
  faBullseye,
  faCalendarAlt,
  faCalendarCheck,
  faCaretSquareLeft,
  faCaretSquareRight,
  faCheckSquare,
  faCogs,
  faDonate,
  faDotCircle,
  faHome,
  faMoneyBill,
  faPaperPlane
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-marathon',
  templateUrl: './marathon.component.html',
  styleUrls: ['./marathon.component.scss']
})
export class MarathonComponent implements OnInit {

  public faHome = faHome;
  public faBook = faBook;
  public faPaperPlane = faPaperPlane;
  public faCalendar = faCalendarAlt;
  public faDonate = faDonate;
  public faMoneyBill = faMoneyBill;
  public faBullseye = faBullseye;
  public faCogs = faCogs;
  public faCheckSquare = faCheckSquare;
  public faCalendarCheck = faCalendarCheck;
  public faDotCircle = faDotCircle;
  public faCaretLeft = faCaretSquareLeft;
  public faCaretRight = faCaretSquareRight;

  public minimized = false;

  constructor(private route: ActivatedRoute,
              public userService: UserService,
              public marathonService: MarathonService) {
    if (!this.marathonService.marathon || this.marathonService.marathon.id !== this.route.snapshot.data.marathon.id) {
      delete this.marathonService.marathon;
      this.marathonService.marathon = {...this.route.snapshot.data.marathon};
    }
  }

  ngOnInit() {
  }

  isAdmin() {
    return this.marathonService.isAdmin(this.userService.user);
  }

}
