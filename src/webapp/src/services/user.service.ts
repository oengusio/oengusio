import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Router } from '@angular/router';
import { User } from '../model/user';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { Observable } from 'rxjs';
import { ValidationErrors } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { UserProfile } from '../model/user-profile';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private _user: User;

  constructor(private http: HttpClient,
              private router: Router,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  getRedirectUri() {
    return encodeURIComponent(environment.loginRedirect);
  }

  getSyncRedirectUri() {
    return encodeURIComponent(environment.syncRedirect);
  }

  getTwitchLoginClientId() {
    return environment.twitchLoginClientId;
  }

  getTwitchSyncClientId() {
    return environment.twitchSyncClientId;
  }

  login(service: string, code?: string, oauthToken?: string, oauthVerifier?: string): Observable<any> {
    return this.http.post(environment.api + '/user/login', {
      service: service,
      code: code,
      oauthToken: oauthToken,
      oauthVerifier: oauthVerifier
    });
  }

  sync(service: string, code?: string, oauthToken?: string, oauthVerifier?: string): Observable<any> {
    return this.http.post(environment.api + '/user/sync', {
      service: service,
      code: code,
      oauthToken: oauthToken,
      oauthVerifier: oauthVerifier
    });
  }

  logout() {
    this._user = null;
    // TODO: tracker this.matomoTracker.setUserId(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/']);
  }

  getMe(): Observable<User> {
    return this.http.get<User>(environment.api + '/user/me');
  }

  me() {
    return this.getMe().subscribe((response: User) => {
      this._user = response;
      // TODO: tracker this.matomoTracker.setUserId(this._user.username);
      if (!this._user.mail) {
        this.router.navigate(['user/new']);
      }
    }, error => {
      this._user = null;
    });
  }

  update(user: User) {
    return this.http.patch(environment.api + '/user/' + user.id, user).subscribe(() => {
      if (!user.enabled) {
        this.translateService.get('alert.user.deactivate.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
        });
        this.logout();
        return;
      }
      this._user = {...this._user, ...user};
      localStorage.setItem('user', JSON.stringify(this._user));
      this.translateService.get('alert.user.update.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
    }, () => {
      this.translateService.get('alert.user.update.error').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-warning'
        };
        this.toastr.open(alertConfig);
      });
    });
  }

  exists(name: string): Observable<ValidationErrors> {
    return this.http.get<ValidationErrors>(environment.api + '/user/' + name + '/exists');
  }

  search(name: string): Observable<User[]> {
    return this.http.get<User[]>(environment.api + '/user/' + name + '/search');
  }

  getProfile(name: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(environment.api + '/user/' + name);
  }

  isBanned(): boolean {
    return this._user.roles.includes('ROLE_BANNED');
  }

  set token(value: string) {
    localStorage.setItem('token', value);
  }

  get token(): string {
    return localStorage.getItem('token');
  }

  get user(): User {
    return this._user;
  }
}
