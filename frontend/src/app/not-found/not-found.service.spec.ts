import { TestBed } from '@angular/core/testing';

import { NotFoundService } from './not-found.service';

describe('NotFoundService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: NotFoundService = TestBed.get(NotFoundService);
    expect(service).toBeTruthy();
  });
});
