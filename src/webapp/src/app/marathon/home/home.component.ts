import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { faDiscord, faTwitch, faTwitter, faYoutube } from '@fortawesome/free-brands-svg-icons';
import { faGlobe } from '@fortawesome/free-solid-svg-icons';
import isoLang from '../../../assets/languages.json';
import { MarathonService } from '../../../services/marathon.service';
import { UserService } from '../../../services/user.service';

declare const Twitch: any;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  public moment = moment;
  public languages = (<any>isoLang);

  public timezone = moment.tz.guess();

  public faTwitch = faTwitch;
  public faTwitter = faTwitter;
  public faDiscord = faDiscord;
  public faYoutube = faYoutube;
  public faGlobe = faGlobe;

  constructor(public marathonService: MarathonService,
              public userService: UserService) {
  }

  ngOnInit() {
    if (this.isLive()) {
      const player = new Twitch.Embed('twitch-embed', {
        width: 854,
        height: 480,
        channel: this.marathonService.marathon.twitch
      });
    }
  }

  isLive() {
    const now = moment();
    return moment(this.marathonService.marathon.startDate).isBefore(now)
      && moment(this.marathonService.marathon.endDate).isAfter(now)
      && !!this.marathonService.marathon.twitch;
  }

}
