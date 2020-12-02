import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';
import { Schedule } from '../model/schedule';
import moment from 'moment-timezone';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  constructor(private http: HttpClient,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  getAllForMarathon(marathonId: string): Observable<Schedule> {
    return this.http.get<Schedule>(environment.api + '/marathon/' + marathonId + '/schedule');
  }

  save(marathonId: string, schedule: Schedule) {
    return this.http.put(environment.api + '/marathon/' + marathonId + '/schedule', schedule, {observe: 'response'})
      .subscribe((response: any) => {
        this.translateService.get('alert.schedule.save.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
      });
    }, error => {
      this.translateService.get('alert.schedule.save.error').subscribe((res: string) => {
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

  exportAllForMarathon(marathonId: string, format: string) {
    const exportUrl = environment.api + '/marathon/' + marathonId + '/schedule/export?format=' + format + '&zoneId='
      + moment.tz.guess() + '&locale=' + localStorage.getItem('language');
    // TODO: tracker this.matomoTracker.trackLink(exportUrl, 'download');
    this.http.get(exportUrl, {responseType: 'text'})
      .subscribe(response => {
          const blob = new Blob([response], {type: 'text/csv'});
          const url = window.URL.createObjectURL(blob);

          if (navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, marathonId + '-schedule.' + format);
          } else {
            const a = document.createElement('a');
            a.href = url;
            a.download = marathonId + '-schedule.' + format;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
          }
          window.URL.revokeObjectURL(url);
        },
        error => {
          this.translateService.get('alert.schedule.export.error').subscribe((res: string) => {
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
