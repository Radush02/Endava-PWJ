import { TestBed } from '@angular/core/testing';

import { TestcaseService } from './testcase.service';

describe('TestcaseService', () => {
  let service: TestcaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TestcaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
