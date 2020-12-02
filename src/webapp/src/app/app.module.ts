import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { httpInterceptorProviders } from '../interceptors';
import { FormsModule } from '@angular/forms';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from '@busacca/ng-pick-datetime';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NwbAlertModule, NwbCommonModule, NwbSwitchModule } from '@wizishop/ng-wizi-bulma';
import { MarathonModule } from './marathon/marathon.module';
import { DirectivesModule } from './directives/directives.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { HomepageComponent } from './homepage/homepage.component';
import { HomepageMetadataResolver } from './resolvers/next-marathons-resolver';
import { UserModule } from './user/user.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { OengusCommonModule } from './oengus-common/oengus-common.module';
import { AboutComponent } from './about/about.component';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarComponent } from './calendar/calendar.component';
import { WebpackTranslateLoader } from '../loader/webpack-translate-loader';

const appRoutes: Routes = [
  {path: 'login/:service', component: LoginComponent},
  {
    path: '',
    component: HomepageComponent,
    resolve: {
      homepageMetadata: HomepageMetadataResolver
    }
  },
  {
    path: 'calendar',
    component: CalendarComponent
  },
  {
    path: 'about',
    component: AboutComponent
  },
  {path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomepageComponent,
    AboutComponent,
    CalendarComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(appRoutes),
    HttpClientModule,
    FormsModule,
    OwlDateTimeModule,
    OwlNativeDateTimeModule,
    NwbSwitchModule,
    NwbAlertModule,
    MarathonModule,
    UserModule,
    DirectivesModule,
    FontAwesomeModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: WebpackTranslateLoader
      }
    }),
    OengusCommonModule,
    NwbCommonModule,
    FullCalendarModule
  ],
  exports: [RouterModule],
  providers: [httpInterceptorProviders,
    HomepageMetadataResolver],
  bootstrap: [AppComponent]
})
export class AppModule {
}

