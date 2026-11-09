import { TestBed } from '@angular/core/testing';

import { IndexDevService } from './index-dev.service';

describe('IndexDevService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: IndexDevService = TestBed.get(IndexDevService);
    expect(service).toBeTruthy();
  });
});
