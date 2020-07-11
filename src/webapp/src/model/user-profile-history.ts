import { Game } from './game';
import { Opponent } from './opponent';

export class UserProfileHistory {

  marathonId: string;
  marathonName: string;
  marathonStartDate: Date;
  visible: boolean;
  games: Game[];
  opponentDtos: Opponent[];
  status: string;

}
