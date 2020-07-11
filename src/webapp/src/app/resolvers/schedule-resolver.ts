import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { ScheduleService } from '../../services/schedule.service';
import { Schedule } from '../../model/schedule';

@Injectable()
export class ScheduleResolver implements Resolve<Schedule> {

  constructor(private scheduleService: ScheduleService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<Schedule> | Promise<Schedule> | Schedule {
    return this.scheduleService.getAllForMarathon(route.parent.paramMap.get('id')).toPromise().catch(() => {
      return new Schedule();
    });
  }
}
