import { Injectable } from '@angular/core';
import { Submission } from '../model/submission';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class SubmissionService {

  constructor(private http: HttpClient,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  mine(marathonId: string): Observable<Submission> {
    return this.http.get<Submission>(environment.api + '/marathon/' + marathonId + '/submission/me');
  }

  availabilities(marathonId: string): Observable<any> {
    return this.http.get<any>(environment.api + '/marathon/' + marathonId + '/submission/availabilities');
  }

  answers(marathonId: string): Observable<Submission[]> {
    return this.http.get<Submission[]>(environment.api + '/marathon/' + marathonId + '/submission/answers');
  }

  availabilitiesForUser(marathonId: string, userId: number): Observable<any> {
    return this.http.get<any>(environment.api + '/marathon/' + marathonId + '/submission/availabilities/' + userId);
  }

  create(marathonId: string, submission: Submission) {
    return this.http
      .post(environment.api + '/marathon/' + marathonId + '/submission', submission, {observe: 'response'})
      .subscribe((response: any) => {
        this.translateService.get('alert.submission.save.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
        });
      }, error => {
        this.translateService.get('alert.submission.save.error').subscribe((res: string) => {
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

  update(marathonId: string, submission: Submission) {
    return this.http
      .put(environment.api + '/marathon/' + marathonId + '/submission', submission, {observe: 'response'})
      .subscribe((response: any) => {
        this.translateService.get('alert.submission.save.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
        });
      }, error => {
        this.translateService.get('alert.submission.save.error').subscribe((res: string) => {
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

  delete(marathonId: string, submissionId: number, callback?) {
    return this.http.delete(environment.api + '/marathon/' + marathonId + '/submission/' + submissionId).subscribe(response => {
      this.translateService.get('alert.submission.deletion.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
        if (callback) {
          callback();
        }
      });
    }, error => {
      this.translateService.get('alert.submission.deletion.error').subscribe((res: string) => {
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

}
