import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Marathon } from '../../model/marathon';
import { Observable } from 'rxjs';
import { MarathonService } from '../../services/marathon.service';

@Injectable()
export class MarathonResolver implements Resolve<Marathon> {

  constructor(private marathonService: MarathonService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<Marathon> | Promise<Marathon> | Marathon {
    return this.marathonService.find(route.paramMap.get('id'));
  }
}
