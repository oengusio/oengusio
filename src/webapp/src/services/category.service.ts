import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NwbAlertConfig, NwbAlertService } from '@wizishop/ng-wizi-bulma';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Opponent } from '../model/opponent';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(private http: HttpClient,
              private toastr: NwbAlertService,
              private translateService: TranslateService) {
  }

  getFromCode(marathonId: string, code: string): Observable<Opponent> {
    return this.http.get<Opponent>(environment.api + '/marathon/' + marathonId + '/category/' + code);
  }

  delete(marathonId: string, submissionId: number) {
    return this.http.delete(environment.api + '/marathon/' + marathonId + '/category/' + submissionId).subscribe(response => {
      this.translateService.get('alert.category.deletion.success').subscribe((res: string) => {
        const alertConfig: NwbAlertConfig = {
          message: res,
          duration: 3000,
          position: 'is-right',
          color: 'is-success'
        };
        this.toastr.open(alertConfig);
      });
    }, error => {
      this.translateService.get('alert.category.deletion.error').subscribe((res: string) => {
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
