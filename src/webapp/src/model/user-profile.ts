import { UserProfileHistory } from './user-profile-history';
import { Marathon } from './marathon';

export class UserProfile {

  username: string;
  usernameJapanese: string;
  enabled: boolean;
  twitterName: string;
  twitchName: string;
  speedruncomName: string;
  history: UserProfileHistory[];
  moderatedMarathons: Marathon[];

}
