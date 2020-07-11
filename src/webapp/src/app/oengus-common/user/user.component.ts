import { Component, Input, OnInit } from '@angular/core';
import { User } from '../../../model/user';
import { faTwitch, faTwitter } from '@fortawesome/free-brands-svg-icons';
import { faTrophy } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  @Input() user: User;
  @Input() showSocialLinks = false;
  @Input() showProfileLink = true;

  public faTwitch = faTwitch;
  public faTwitter = faTwitter;
  public faTrophy = faTrophy;

  public localStorage = localStorage;

  constructor() {
  }

  ngOnInit() {
  }

}
