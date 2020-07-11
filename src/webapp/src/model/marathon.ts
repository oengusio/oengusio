import { User } from './user';
import { Question } from './question';

export class Marathon {

  id: string;
  name: string;
  creator: User;
  startDate: Date;
  endDate: Date;
  submissionsStartDate: Date;
  submissionsEndDate: Date;
  description: string;
  onsite: boolean;
  location: string;
  language: string;
  maxGamesPerRunner: number;
  maxCategoriesPerGame: number;
  hasMultiplayer: boolean;
  maxNumberOfScreens: number;
  twitch: string;
  twitter: string;
  discord: string;
  youtube: string;
  country: string;
  discordPrivacy: boolean;
  submitsOpen: boolean;
  moderators: User[];
  defaultSetupTime: string;
  defaultSetupTimeHuman: string;
  selectionDone: boolean;
  scheduleDone: boolean;
  isPrivate: boolean;
  hasIncentives: boolean;
  canEditSubmissions: boolean;
  questions: Question[];
  hasDonations: boolean;
  payee: string;
  donationCurrency: string;
  supportedCharity: string;
  donationWebhook: string;
  donationsTotal: number;
  hasSubmitted: boolean;
  donationsOpen: boolean;
  videoRequired: boolean;
  unlimitedGames: boolean;
  unlimitedCategories: boolean;
  emulatorAuthorized: boolean;

  constructor() {
    this.onsite = false;
    this.maxGamesPerRunner = 5;
    this.maxCategoriesPerGame = 3;
    this.maxNumberOfScreens = 4;
    this.discordPrivacy = false;
    this.submitsOpen = false;
    this.selectionDone = false;
    this.donationsOpen = false;
    this.videoRequired = true;
    this.unlimitedGames = false;
    this.unlimitedCategories = false;
    this.emulatorAuthorized = true;
    this.isPrivate = true;
    this.questions = [];
  }
}
