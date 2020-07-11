import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Selection } from '../model/selection';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class SelectionService {

  constructor(private http: HttpClient,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  getAllForMarathon(marathonId: string, statuses = []): Observable<Map<number, Selection>> {
    const params = new HttpParams().set('status', statuses.join(','));
    return this.http.get<Map<number, Selection>>(environment.api + '/marathon/' + marathonId + '/selection', {params: params});
  }

  save(marathonId: string, selection: Selection[]) {
    return this.http.put(environment.api + '/marathon/' + marathonId + '/selection', selection).subscribe((response: any) => {
      this.translateService.get('alert.selection.save.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
    }, error => {
      this.translateService.get('alert.selection.save.error').subscribe((res: string) => {
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
