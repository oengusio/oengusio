import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { NewUserComponent } from './new-user/new-user.component';
import { SettingsComponent } from './settings/settings.component';
import { FormsModule } from '@angular/forms';
import { DirectivesModule } from '../directives/directives.module';
import { UserResolver } from '../resolvers/user-resolver';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { HttpLoaderFactory } from '../../services/http-loader-factory';
import { UsernameJapaneseExistsValidatorDirective } from '../directives/username-japanese-exists-validator.directive';
import { ProfileComponent } from './profile/profile.component';
import { UserProfileResolver } from '../resolvers/user-profile-resolver';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

const userRoutes: Routes = [
  {
    path: 'user/settings', component: SettingsComponent, resolve: {
      user: UserResolver
    }
  },
  {
    path: 'user/settings/sync/:service', component: SettingsComponent, resolve: {
      user: UserResolver
    }
  },
  {
    path: 'user/profile/:name', component: ProfileComponent, resolve: {
      user: UserProfileResolver
    }
  },
  {path: 'user/new', component: NewUserComponent}
];

@NgModule({
  declarations: [
    NewUserComponent,
    SettingsComponent,
    UsernameJapaneseExistsValidatorDirective,
    ProfileComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(userRoutes),
    FormsModule,
    DirectivesModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    FontAwesomeModule
  ],
  providers: [UserResolver, UserProfileResolver]
})
export class UserModule {
}
