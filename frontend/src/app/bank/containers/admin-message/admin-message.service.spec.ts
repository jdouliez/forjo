import { TestBed } from '@angular/core/testing';

import { AdminMessageService } from './admin-message.service';

describe('AdminMessageService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AdminMessageService = TestBed.get(AdminMessageService);
    expect(service).toBeTruthy();
  });
});
