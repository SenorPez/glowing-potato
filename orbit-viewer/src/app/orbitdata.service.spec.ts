import { TestBed } from '@angular/core/testing';

import { OrbitdataService } from './orbitdata.service';

describe('OrbitdataService', () => {
  let service: OrbitdataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrbitdataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
