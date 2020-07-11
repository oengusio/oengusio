import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.route.queryParams.subscribe(queryParams => {
        this.userService.login(params['service'],
          queryParams['code'],
          queryParams['oauth_token'],
          queryParams['oauth_verifier']).subscribe(response => {
          this.userService.token = response.token;
          this.userService.me().add(() => {
            if (!this.userService.user.mail) {
              this.router.navigate(['user/new']);
            } else {
              this.router.navigate(['/']);
            }
          });
        }, error => {
          switch (error.error.code) {
            case 'USERNAME_EXISTS':
              this.translateService.get('alert.user.login.usernameExists').subscribe((res: string) => {
                const alertConfig: NwbAlertConfig = {
                  message: res,
                  duration: 5000,
                  position: 'is-right',
                  color: 'is-warning'
                };
                this.toastr.open(alertConfig);
              });
              break;
            case 'DISABLED_ACCOUNT':
              this.translateService.get('alert.user.login.disabledAccount').subscribe((res: string) => {
                const alertConfig: NwbAlertConfig = {
                  message: res,
                  duration: 5000,
                  position: 'is-right',
                  color: 'is-warning'
                };
                this.toastr.open(alertConfig);
              });
              break;
            default:
              this.translateService.get('alert.user.login.error').subscribe((res: string) => {
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
          this.router.navigate(['/']);
        });
      });
    });

  }
}
