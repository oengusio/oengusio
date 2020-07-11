import { ScheduleLine } from './schedule-line';
import { Bid } from './bid';

export class Incentive {

  id: number;
  scheduleLine: ScheduleLine;
  name: string;
  description: string;
  bidWar: boolean;
  locked: boolean;
  goal: number;
  currentAmount: number;
  bids: Bid[];
  toDelete: boolean;
  openBid: boolean;

  constructor() {
    this.scheduleLine = new ScheduleLine();
    this.currentAmount = 0;
    this.bidWar = false;
    this.locked = false;
    this.openBid = false;
    this.bids = [];
  }
}
