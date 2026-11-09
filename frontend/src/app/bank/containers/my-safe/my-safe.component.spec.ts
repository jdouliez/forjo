import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MySafeComponent } from './my-safe.component';

describe('MySafeComponent', () => {
  let component: MySafeComponent;
  let fixture: ComponentFixture<MySafeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MySafeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MySafeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
