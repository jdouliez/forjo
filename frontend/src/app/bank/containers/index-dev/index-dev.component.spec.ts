import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IndexDevComponent } from './index-dev.component';

describe('IndexDevComponent', () => {
  let component: IndexDevComponent;
  let fixture: ComponentFixture<IndexDevComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IndexDevComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexDevComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
