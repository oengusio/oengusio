import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[appMaxNumberValidator]',
  providers: [{provide: NG_VALIDATORS, useExisting: MaxNumberValidatorDirective, multi: true}]
})
export class MaxNumberValidatorDirective implements Validator {

  @Input('appMaxNumberValidator') maxNumber: number;

  constructor() {
  }

  registerOnValidatorChange(fn: () => void): void {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    if (control.value <= this.maxNumber) {
      return null;
    } else {
      return {
        maxNumber: true
      };
    }
  }

}
