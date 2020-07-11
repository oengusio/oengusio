import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsernameExistsValidatorDirective } from './username-exists-validator.directive';
import { MarathonExistsValidatorDirective } from './marathon-exists-validator.directive';
import { MinNumberValidatorDirective } from './min-number-validator.directive';
import { MinDurationValidatorDirective } from './min-duration-validator.directive';
import { MaxNumberValidatorDirective } from './max-number-validator.directive';
import { TotalValidatorDirective } from './total-validator.directive';

@NgModule({
  declarations: [
    UsernameExistsValidatorDirective,
    MarathonExistsValidatorDirective,
    MinNumberValidatorDirective,
    MaxNumberValidatorDirective,
    MinDurationValidatorDirective,
    TotalValidatorDirective
  ],
  imports: [
    CommonModule
  ],
  exports: [
    UsernameExistsValidatorDirective,
    MarathonExistsValidatorDirective,
    MinNumberValidatorDirective,
    MaxNumberValidatorDirective,
    TotalValidatorDirective
  ]
})
export class DirectivesModule {
}
