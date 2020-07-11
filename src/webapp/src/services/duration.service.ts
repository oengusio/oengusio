import { Injectable } from '@angular/core';
import moment from 'moment';

@Injectable({
  providedIn: 'root'
})
export class DurationService {

  constructor() {
  }

  static toHuman(estimate: string): string {
    const duration = moment.duration(estimate);
    const hours = Math.floor(duration.asHours()).toString().padStart(2, '0');
    const minutes = duration.minutes().toString().padStart(2, '0');
    const seconds = duration.seconds().toString().padStart(2, '0');
    return '' + hours + ':' + minutes + ':' + seconds;
  }

}
