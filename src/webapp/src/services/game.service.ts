import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Game } from '../model/game';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private http: HttpClient,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  getAllForMarathon(marathonId: string): Observable<Game[]> {
    return this.http.get<Game[]>(environment.api + '/marathon/' + marathonId + '/game');
  }

  exportAllForMarathon(marathonId: string) {
    const exportUrl = environment.api + '/marathon/' + marathonId + '/game/export?locale='
      + localStorage.getItem('language') + '&zoneId=' + moment.tz.guess();
    // TODO: tracker this.matomoTracker.trackLink(exportUrl, 'download');
    this.http.get(exportUrl, {responseType: 'text'})
      .subscribe(response => {
          const blob = new Blob([response], {type: 'text/csv'});
          const url = window.URL.createObjectURL(blob);

          if (navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, marathonId + '-submissions.csv');
          } else {
            const a = document.createElement('a');
            a.href = url;
            a.download = marathonId + '-submissions.csv';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
          }
          window.URL.revokeObjectURL(url);
        },
        error => {
          this.translateService.get('alert.game.export.error').subscribe((res: string) => {
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

  delete(marathonId: string, submissionId: number) {
    return this.http.delete(environment.api + '/marathon/' + marathonId + '/game/' + submissionId).subscribe(response => {
      this.translateService.get('alert.game.deletion.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
    }, error => {
      this.translateService.get('alert.game.deletion.error').subscribe((res: string) => {
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
