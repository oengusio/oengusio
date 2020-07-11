import { Category } from './category';
import { User } from './user';

export class Game {

  id: number;
  name: string;
  description: string;
  console: string;
  ratio: string;
  categories: Category[];
  user: User;
  emulated: boolean;
  visible: boolean;
  submissionId: number;
  status: string;

  constructor() {
    this.categories = [];
    this.user = new User();
  }
}
