import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IncentiveManagementComponent } from './incentive-management.component';

describe('IncentiveManagementComponent', () => {
  let component: IncentiveManagementComponent;
  let fixture: ComponentFixture<IncentiveManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IncentiveManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IncentiveManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
