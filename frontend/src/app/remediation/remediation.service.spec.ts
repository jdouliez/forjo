import { TestBed } from '@angular/core/testing';

import { RemediationService } from './remediation.service';

describe('RemediationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RemediationService = TestBed.get(RemediationService);
    expect(service).toBeTruthy();
  });
});
