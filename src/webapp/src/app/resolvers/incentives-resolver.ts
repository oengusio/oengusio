import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { IncentiveService } from '../../services/incentive.service';
import { Incentive } from '../../model/incentive';

@Injectable()
export class IncentivesResolver implements Resolve<Incentive[]> {

  constructor(private incentiveService: IncentiveService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Incentive[]> | Promise<Incentive[]> | Incentive[] {
    return this.incentiveService.getAllForMarathon(route.parent.paramMap.get('id'), route.data['withLocked'], route.data['withUnapproved']);
  }
}
