import { Marathon } from './marathon';
import { ScheduleLine } from './schedule-line';

export class Schedule {

  id: number;
  marathon?: Marathon;
  lines: ScheduleLine[];

  constructor() {
    this.lines = [];
  }
}
