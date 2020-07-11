import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-monetary-amount',
  templateUrl: './monetary-amount.component.html',
  styleUrls: ['./monetary-amount.component.scss']
})
export class MonetaryAmountComponent implements OnInit {

  @Input() amount: number;
  @Input() currency: string;

  public localStorage = localStorage;

  constructor() {
    if (!this.amount) {
      this.amount = 0;
    }
  }

  ngOnInit() {
  }

}
