import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Incentive } from '../model/incentive';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class IncentiveService {

  constructor(private http: HttpClient,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  getAllForMarathon(marathonId: string, withLocked = true, withUnapproved = true): Observable<Incentive[]> {
    return this.http.get<Incentive[]>(environment.api + '/marathon/' + marathonId + '/incentive?withLocked='
      + withLocked + '&withUnapproved=' + withUnapproved);
  }

  saveAll(marathonId: string, incentives: Incentive[]) {
    return this.http.post(environment.api + '/marathon/' + marathonId + '/incentive', incentives, {observe: 'response'})
      .subscribe((response: any) => {
        this.translateService.get('alert.incentives.save.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
        });
      }, error => {
        this.translateService.get('alert.incentives.save.error').subscribe((res: string) => {
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

  delete(marathonId: string, incentiveId: number) {
    return this.http.delete(environment.api + '/marathon/' + marathonId + '/incentive/' + incentiveId)
      .subscribe((response: any) => {
        this.translateService.get('alert.incentives.delete.success').subscribe((res: string) => {
          const alertConfig: NwbAlertConfig = {
            message: res,
            duration: 3000,
            position: 'is-right',
            color: 'is-success'
          };
          this.toastr.open(alertConfig);
        });
      }, error => {
        this.translateService.get('alert.incentives.delete.error').subscribe((res: string) => {
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
