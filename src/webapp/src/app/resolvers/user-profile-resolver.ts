import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../model/user-profile';

@Injectable()
export class UserProfileResolver implements Resolve<UserProfile> {

  constructor(private userService: UserService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<UserProfile> | Promise<UserProfile> | UserProfile {
    return this.userService.getProfile(route.paramMap.get('name'));
  }
}
