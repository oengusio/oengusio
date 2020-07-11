import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { DonationService } from '../../services/donation.service';
import { DonationStats } from '../../model/donation-stats';

@Injectable()
export class DonationsStatsResolver implements Resolve<DonationStats> {

  constructor(private donationService: DonationService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<DonationStats> | Promise<DonationStats> | DonationStats {
    return this.donationService.findStats(route.parent.paramMap.get('id'));
  }
}
