import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { MarathonService } from '../../services/marathon.service';
import { HomepageMetadata } from '../../model/homepage-metadata';

@Injectable()
export class HomepageMetadataResolver implements Resolve<HomepageMetadata> {

  constructor(private marathonService: MarathonService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<HomepageMetadata> | Promise<HomepageMetadata> | HomepageMetadata {
    return this.marathonService.findHomepageMetadata();
  }
}
