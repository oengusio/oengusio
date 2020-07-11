export class Bid {

  id: number;
  name: string;
  currentAmount: number;
  approved: boolean;
  isNew = false;
  incentiveId: number;
  toDelete = false;

  constructor() {
    this.currentAmount = 0;
  }
}
