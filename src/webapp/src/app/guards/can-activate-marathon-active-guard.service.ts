import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { MarathonService } from '../../services/marathon.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CanActivateMarathonActiveGuard implements CanActivate {

  constructor(private marathonService: MarathonService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (!this.marathonService.marathon) {
      return new Promise<boolean>((resolve, reject) => {
        resolve(this.marathonService.find(route.parent.paramMap.get('id')).pipe(
          map((marathon) => {
            return !this.marathonService.isArchived(marathon);
          })
        ).toPromise());
      });
    }
    return !this.marathonService.isArchived();
  }
}
