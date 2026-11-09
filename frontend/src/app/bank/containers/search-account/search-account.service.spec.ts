import { TestBed } from '@angular/core/testing';

import { SearchAccountService } from './search-account.service';

describe('SearchAccountService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SearchAccountService = TestBed.get(SearchAccountService);
    expect(service).toBeTruthy();
  });
});
