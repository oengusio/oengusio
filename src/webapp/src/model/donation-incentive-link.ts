import { Incentive } from './incentive';
import { Bid } from './bid';

export class DonationIncentiveLink {

  id: number;
  incentive: Incentive;
  bid: Bid;
  amount: number;

  constructor() {
  }
}
