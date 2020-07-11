import { DonationExtraData } from './donation-extra-data';
import { DonationIncentiveLink } from './donation-incentive-link';

export class Donation {

	id: number;
	nickname: string;
	firstName: string;
	lastName: string;
	address: string;
	zipcode: string;
	city: string;
	country: string;
	date: Date;
	amount: number;
	comment: string;
	answers: DonationExtraData[];
	donationIncentiveLinks: DonationIncentiveLink[];

	constructor() {
		this.answers = [];
		this.donationIncentiveLinks = [];
	}
}
