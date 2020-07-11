import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { SelectionService } from '../../services/selection.service';
import { Selection } from '../../model/selection';

@Injectable()
export class SelectionResolver implements Resolve<Map<number, Selection>> {

  constructor(private selectionService: SelectionService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<Map<number, Selection>> | Promise<Map<number, Selection>> | Map<number, Selection> {
    return this.selectionService.getAllForMarathon(route.parent.paramMap.get('id'), route.data['statuses']).toPromise().catch(() => {
      return new Map();
    });
  }
}
