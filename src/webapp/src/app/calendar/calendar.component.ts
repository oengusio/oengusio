import { Component, OnInit } from '@angular/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import { MarathonService } from '../../services/marathon.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {

  public calendarPlugins = [dayGridPlugin];
  public localStorage = localStorage;
  public events = [];

  constructor(private marathonService: MarathonService,
              private router: Router) {
  }

  ngOnInit() {
  }

  fetchMarathons(info) {
    this.marathonService.findForMonth(info.view.activeStart, info.view.activeEnd).subscribe(response => {
      this.events = [];
      response.forEach(marathon => {
        this.events.push({
          id: marathon.id,
          title: marathon.name,
          start: marathon.startDate,
          end: marathon.endDate
        });
      });
    });
  }

  goToEvent(eventClickInfo) {
    this.router.navigate(['/marathon', eventClickInfo.event.id]);
  }

}
