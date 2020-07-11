import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Marathon } from '../model/marathon';
import { environment } from '../environments/environment';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { Observable, Subscription } from 'rxjs';
import { ValidationErrors } from '@angular/forms';
import { UserService } from './user.service';
import { HomepageMetadata } from '../model/homepage-metadata';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class MarathonService {
  private _marathon: Marathon;

  get marathon(): Marathon {
    return this._marathon;
  }

  set marathon(value: Marathon) {
    this._marathon = value;
  }

  constructor(private http: HttpClient,
              private router: Router,
              private toastr: NwbAlertService,
              private userService: UserService,
              private translateService: TranslateService) {
  }

  create(marathon: Marathon): Subscription {
    marathon.creator = this.userService.user;
    return this.http.put(environment.api + '/marathon', marathon, {observe: 'response'}).subscribe((response: any) => {
      this.router.navigate(['/marathon/' + response.headers.get('Location')]);

      this.translateService.get('alert.marathon.creation.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
    }, error => {
      this.translateService.get('alert.marathon.creation.error').subscribe((res: string) => {
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

  update(marathon: Marathon, showToaster: boolean = true) {
    return this.http.patch(environment.api + '/marathon/' + marathon.id, marathon).subscribe(() => {
      if (showToaster) {
        this.translateService.get('alert.marathon.update.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
        });
      }

      this._marathon = {...marathon};
    }, error => {
      this.translateService.get('alert.marathon.update.error').subscribe((res: string) => {
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
    return this.http.get<ValidationErrors>(environment.api + '/marathon/' + name + '/exists');
  }

  find(name: string): Observable<Marathon> {
    return this.http.get<Marathon>(environment.api + '/marathon/' + name);
  }

  delete(name: string) {
    this.http.delete(environment.api + '/marathon/' + name).subscribe(response => {
      this.translateService.get('alert.marathon.deletion.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
      this.router.navigate(['/']);
    }, error => {
      this.translateService.get('alert.marathon.deletion.error').subscribe((res: string) => {
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

  findHomepageMetadata(): Observable<HomepageMetadata> {
    return this.http.get<HomepageMetadata>(environment.api + '/marathon');
  }

  findForMonth(start: Date, end: Date): Observable<Marathon[]> {
    return this.http.get<Marathon[]>(environment.api + '/marathon/forDates?start=' + start.toISOString() + '&end=' + end.toISOString() +
      '&zoneId=' + moment.tz.guess());
  }

  isArchived(marathon: Marathon = this._marathon): boolean {
    return moment(marathon.endDate).isBefore(moment());
  }

  isAdmin(user: User): boolean {
    if (!user) {
      return false;
    }
    return user.id === this.marathon.creator.id ||
      !!this.marathon.moderators.find(u => u.id === user.id) ||
      user.roles.includes('ROLE_ADMIN');
  }

  hasDstChange(): boolean {
    return moment(this.marathon.startDate).isDST() !== moment(this.marathon.endDate).isDST();
  }
}
