import { TestBed } from '@angular/core/testing';

import { AdminHoneyService } from './admin-honey.service';

describe('AdminHoneyService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AdminHoneyService = TestBed.get(AdminHoneyService);
    expect(service).toBeTruthy();
  });
});
