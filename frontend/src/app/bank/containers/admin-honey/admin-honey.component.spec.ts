import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminHoneyComponent } from './admin-honey.component';

describe('AdminHoneyComponent', () => {
  let component: AdminHoneyComponent;
  let fixture: ComponentFixture<AdminHoneyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminHoneyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminHoneyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
