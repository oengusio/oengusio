import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewMarathonComponent } from './new-marathon.component';

describe('NewMarathonComponent', () => {
  let component: NewMarathonComponent;
  let fixture: ComponentFixture<NewMarathonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewMarathonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewMarathonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
