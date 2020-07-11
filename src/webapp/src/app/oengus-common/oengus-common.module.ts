import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user/user.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MonetaryAmountComponent } from './monetary-amount/monetary-amount.component';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [UserComponent, MonetaryAmountComponent],
  exports: [
    UserComponent,
    MonetaryAmountComponent
  ],
  imports: [
    CommonModule,
    FontAwesomeModule,
    RouterModule
  ]
})
export class OengusCommonModule {
}
