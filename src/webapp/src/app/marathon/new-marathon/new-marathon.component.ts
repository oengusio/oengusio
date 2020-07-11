import { Component, OnInit } from '@angular/core';
import { Marathon } from '../../../model/marathon';
import { MarathonService } from '../../../services/marathon.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-new-marathon',
  templateUrl: './new-marathon.component.html',
  styleUrls: ['./new-marathon.component.css']
})
export class NewMarathonComponent implements OnInit {

  public marathon: Marathon;
  public now: Date;
  public env = environment;
  public loading = false;

  constructor(public marathonService: MarathonService) {
    this.now = new Date();
    this.now.setSeconds(0);
  }

  ngOnInit() {
    this.marathon = new Marathon();
  }

  submit() {
    this.loading = true;
    this.marathonService.create(this.marathon).add(() => {
      this.loading = false;
    });
  }

}
