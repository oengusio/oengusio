import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';
import { Donation } from '../model/donation';
import { environment } from '../environments/environment';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Page } from '../model/page';
import { DonationStats } from '../model/donation-stats';
import moment from 'moment-timezone';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  constructor(private http: HttpClient,
              private router: Router,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  find(marathonId: string, page: number, size: number): Observable<Page<Donation>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<Page<Donation>>(environment.api + '/marathon/' + marathonId + '/donation', {
      params: params
    });
  }

  isWebhookOnline(marathonId: string, url: string): Observable<any> {
    const params = new HttpParams().set('url', url);
    return this.http.get(environment.api + '/marathon/' + marathonId + '/donation/webhook', {
      params: params
    });
  }

  findStats(marathonId: string): Observable<DonationStats> {
    return this.http.get<DonationStats>(environment.api + '/marathon/' + marathonId + '/donation/stats');
  }

  donate(marathonId: string, donation: Donation): Observable<any> {
    return this.http.post(environment.api + '/marathon/' + marathonId + '/donation/donate', donation, {observe: 'response'});
  }

  cancel(marathonId: string, orderId: string): Observable<any> {
    return this.http.delete(environment.api + '/marathon/' + marathonId + '/donation/' + orderId);
  }

  validate(marathonId: string, orderId: string) {
    return this.http.post(environment.api + '/marathon/' + marathonId + '/donation/validate/' + orderId, null).subscribe(response => {
      this.translateService.get('alert.donation.validate.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
    }, error => {
      this.translateService.get('alert.donation.validate.error').subscribe((res: string) => {
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

  exportAllForMarathon(marathonId: string) {
    const exportUrl = environment.api + '/marathon/' + marathonId + '/donation/export?zoneId='
      + moment.tz.guess();
    // TODO: tracker this.matomoTracker.trackLink(exportUrl, 'download');
    this.http.get(exportUrl, {responseType: 'text'})
      .subscribe(response => {
          const blob = new Blob([response], {type: 'text/csv'});
          const url = window.URL.createObjectURL(blob);

          if (navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, marathonId + '-donations.csv');
          } else {
            const a = document.createElement('a');
            a.href = url;
            a.download = marathonId + '-donations.csv';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
          }
          window.URL.revokeObjectURL(url);
        },
        error => {
          this.translateService.get('alert.donation.export.error').subscribe((res: string) => {
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
