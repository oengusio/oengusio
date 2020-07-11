import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

marker('alert.submit.DIFFERENT_MARATHON');
marker('alert.submit.NOT_MULTIPLAYER');
marker('alert.submit.SAME_USER');
marker('alert.submit.MAX_SIZE_REACHED');
marker('alert.submit.CODE_NOT_FOUND');
marker('alert.submit.ALREADY_IN_OPPONENTS');
marker('user.profile.filter.temporality.PAST');
marker('user.profile.filter.temporality.FUTURE');

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
