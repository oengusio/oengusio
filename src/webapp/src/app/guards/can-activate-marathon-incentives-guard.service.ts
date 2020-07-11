import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Observable } from 'rxjs';
import { MarathonService } from '../../services/marathon.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CanActivateMarathonIncentivesGuard implements CanActivate {

  constructor(private userService: UserService, private marathonService: MarathonService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (!this.marathonService.marathon) {
      return new Promise<boolean>((resolve, reject) => {
        resolve(this.marathonService.find(route.parent.paramMap.get('id')).pipe(
          map((marathon) => !!marathon.hasIncentives)
        ).toPromise());
      });
    }
    return !!this.marathonService.marathon.hasIncentives;
  }
}
