import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonetaryAmountComponent } from './monetary-amount.component';

describe('MonetaryAmountComponent', () => {
  let component: MonetaryAmountComponent;
  let fixture: ComponentFixture<MonetaryAmountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MonetaryAmountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonetaryAmountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
