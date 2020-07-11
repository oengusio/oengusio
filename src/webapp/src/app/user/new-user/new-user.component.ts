import { Component, OnInit } from '@angular/core';
import { User } from '../../../model/user';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-new-user',
  templateUrl: './new-user.component.html',
  styleUrls: ['./new-user.component.css']
})
export class NewUserComponent implements OnInit {

  public user: User;
  public loading = false;

  constructor(public userService: UserService, private router: Router) {
  }

  ngOnInit() {
    this.user = {...this.userService.user};
  }

  submit() {
    this.loading = true;
    this.userService.update(this.user).add(() => {
      this.loading = false;
      this.router.navigate(['/']);
    });
  }

}
