import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Page } from '../../model/page';
import { Donation } from '../../model/donation';
import { DonationService } from '../../services/donation.service';

@Injectable()
export class DonationsResolver implements Resolve<Page<Donation>> {

  constructor(private donationService: DonationService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<Page<Donation>> | Promise<Page<Donation>> | Page<Donation> {
    return this.donationService.find(route.parent.paramMap.get('id'), 0, 25);
  }
}
