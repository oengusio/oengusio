import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from '../../model/user';
import { UserService } from '../../services/user.service';

@Injectable()
export class UserResolver implements Resolve<User> {

  constructor(private userService: UserService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<User> | Promise<User> | User {
    const user = localStorage.getItem('user');
    if (!user || !JSON.parse(user).id) {
      return this.userService.getMe();
    } else {
      return JSON.parse(user);
    }
  }
}
