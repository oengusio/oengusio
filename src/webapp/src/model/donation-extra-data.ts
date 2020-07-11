import { Question } from './question';

export class DonationExtraData {

  id: number;
  question: Question;
  answer: any;

  constructor() {
    this.question = new Question('DONATION');
  }
}
