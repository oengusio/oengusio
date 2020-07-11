import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[appTotalValidator]',
  providers: [{provide: NG_VALIDATORS, useExisting: TotalValidatorDirective, multi: true}]
})
export class TotalValidatorDirective implements Validator {

  @Input('appTotalValidator') total: number;

  constructor() {
  }

  registerOnValidatorChange(fn: () => void): void {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    const keys = Object.keys(control.value).filter(value => value.startsWith('amount'));
    const inputTotal = keys.map(key => control.value[key]).reduce((acc, cur) => acc + cur, 0);
    if (!this.total || inputTotal <= this.total) {
      return null;
    } else {
      return {
        total: true
      };
    }
  }

}
