import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { UserService } from '../../services/user.service';
import { MarathonService } from '../../services/marathon.service';
import { User } from '../../model/user';
import { Marathon } from '../../model/marathon';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CanActivateMarathonSettingsGuard implements CanActivate {

  constructor(private userService: UserService, private marathonService: MarathonService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (!this.userService.user && !this.marathonService.marathon) {
      return new Promise<boolean>((resolve, reject) => {
        resolve(forkJoin([this.userService.getMe(), this.marathonService.find(route.parent.paramMap.get('id'))])
          .pipe(
            map(([user, marathon]) => {
              return this.condition(user, marathon);
            }),
            catchError(err => of(false))
          ).toPromise());
      });
    }
    return this.condition(this.userService.user, this.marathonService.marathon);
  }

  private condition(user: User, marathon: Marathon) {
    return !!user && !user.roles.includes('ROLE_BANNED') &&
      (marathon.creator.id === user.id ||
        marathon.moderators.findIndex(u => u.id === user.id) >= 0 ||
        user.roles.includes('ROLE_ADMIN'));
  }
}
