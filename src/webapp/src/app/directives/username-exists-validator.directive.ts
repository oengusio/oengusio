import { Directive } from '@angular/core';
import { AbstractControl, AsyncValidator, NG_ASYNC_VALIDATORS, ValidationErrors } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Directive({
  selector: '[appUsernameExistsValidator]',
  providers: [{provide: NG_ASYNC_VALIDATORS, useExisting: UsernameExistsValidatorDirective, multi: true}]
})
export class UsernameExistsValidatorDirective implements AsyncValidator {

  constructor(private userService: UserService) {
  }

  registerOnValidatorChange(fn: () => void): void {
  }

  validate(control: AbstractControl): Observable<ValidationErrors | null> {
    return this.userService.exists(control.value).pipe(map(errors => {
      if (errors.exists && control.value !== this.userService.user.username) {
        return {
          exists: true
        };
      }
      return null;
    }));
  }

}
