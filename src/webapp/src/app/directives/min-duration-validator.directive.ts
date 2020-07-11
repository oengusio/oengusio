import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import moment from 'moment';

@Directive({
  selector: '[appMinDurationValidator]',
  providers: [{provide: NG_VALIDATORS, useExisting: MinDurationValidatorDirective, multi: true}]
})
export class MinDurationValidatorDirective implements Validator {

  @Input('appMinDurationValidator') minDuration: number;

  constructor() {
  }

  registerOnValidatorChange(fn: () => void): void {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    if (moment.duration(control.value).asSeconds() > this.minDuration) {
      return null;
    } else {
      return {
        minDuration: true
      };
    }
  }

}
