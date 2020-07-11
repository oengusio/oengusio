import { User } from './user';

export class ScheduleLine {

  id: number;
  gameName: string;
  console: string;
  emulated: boolean;
  ratio: string;
  categoryName: string;
  estimate: string;
  estimateHuman: string;
  setupTime: string;
  setupTimeHuman: string;
  setupBlock: boolean;
  setupBlockText: string;
  useSetupBlockText: boolean;
  customRun: boolean;
  position: number;
  categoryId: number;
  runners: User[];
  date: Date;
  type: string;

  constructor() {
    this.runners = [];
  }
}
