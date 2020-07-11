import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { SubmissionService } from '../../services/submission.service';
import { Submission } from '../../model/submission';

@Injectable()
export class AnswersResolver implements Resolve<Submission[]> {

  constructor(private submissionService: SubmissionService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<Submission[]> | Promise<Submission[]> | Submission[] {
    return this.submissionService.answers(route.parent.paramMap.get('id')).toPromise().catch(() => {
      return [];
    });
  }
}
