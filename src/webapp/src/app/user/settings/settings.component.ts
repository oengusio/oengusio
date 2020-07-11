import { Component, OnInit } from '@angular/core';
import { User } from '../../../model/user';
import { UserService } from '../../../services/user.service';
import { ActivatedRoute } from '@angular/router';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {

  public user: User;
  public loading = false;

  public deleteConfirm = false;
  public deleteUsername: string;

  constructor(public userService: UserService,
              private route: ActivatedRoute,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
    this.user = {...this.route.snapshot.data.user};
    localStorage.setItem('user', JSON.stringify(this.user));
    this.route.params.subscribe(params => {
      this.route.queryParams.subscribe(queryParams => {
        if (!!params['service'] && (!!queryParams['code'] || !!queryParams['oauth_token'] && !!queryParams['oauth_verifier'])) {
          this.userService.sync(params['service'],
            queryParams['code'],
            queryParams['oauth_token'],
            queryParams['oauth_verifier']).subscribe(response => {
            switch (params['service']) {
              case 'discord' :
                this.user.discordId = response.id;
                this.user.discordName = response.name;
                break;
              case 'twitch' :
                this.user.twitchId = response.id;
                this.user.twitchName = response.name;
                break;
              case 'twitter' :
                this.user.twitterId = response.id;
                this.user.twitterName = response.name;
                break;
            }
            localStorage.setItem('user', JSON.stringify(this.user));
          }, error => {
            switch (error.code) {
              case 'ACCOUNT_ALREADY_SYNCED':
                this.translateService.get('alert.user.sync.alreadySynced').subscribe((res: string) => {
                  const alertConfig: NwbAlertConfig = {
                    message: res,
                    duration: 3000,
                    position: 'is-right',
                    color: 'is-warning'
                  };
                  this.toastr.open(alertConfig);
                });
                break;
              default:
                this.translateService.get('alert.user.sync.error').subscribe((res: string) => {
                  const alertConfig: NwbAlertConfig = {
                    message: res,
                    duration: 3000,
                    position: 'is-right',
                    color: 'is-warning'
                  };
                  this.toastr.open(alertConfig);
                });
                break;
            }
          });
        }
      });
    });
  }

  ngOnInit() {

  }

  unsyncDiscord() {
    delete this.user.discordId;
    delete this.user.discordName;
  }

  unsyncTwitch() {
    delete this.user.twitchId;
  }

  unsyncTwitter() {
    delete this.user.twitterId;
  }

  submit() {
    this.loading = true;
    this.userService.update(this.user).add(() => {
      this.loading = false;
      localStorage.setItem('user', JSON.stringify(this.user));
    });
  }

  syncTwitter() {
    this.userService.sync('twitterAuth').subscribe(response => {
      window.location.replace(response.token);
    });
  }

  deactivate() {
    this.loading = true;
    this.user.enabled = false;
    this.userService.update(this.user).add(() => {
      this.loading = false;
    });
  }

}
