import { TestBed } from '@angular/core/testing';

import { RibService } from './rib.service';

describe('RibService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RibService = TestBed.get(RibService);
    expect(service).toBeTruthy();
  });
});
