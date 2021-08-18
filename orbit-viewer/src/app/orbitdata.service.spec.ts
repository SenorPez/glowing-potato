import { TestBed } from '@angular/core/testing';

import { OrbitdataService } from './orbitdata.service';

describe('OrbitdataService unit tests', () => {
  let service: OrbitdataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrbitdataService);
  });

  it('service should be created', () => {
    expect(service).toBeTruthy();
  });

  describe("lagrangeStability", () => {
    const parameters = [
      {description: "Should pass with solar mass 100 and planet mass 1", solarMass: 100, planetMass: 1, result: true},
      {description: "Should pass with solar mass 50 and planet mass 1", solarMass: 50, planetMass: 1, result: true},
      {description: "Should pass with solar mass 25 and planet mass 1", solarMass: 25, planetMass: 1, result: true},
      {description: "Should fail with solar mass ~24.959 and planet mass 1", solarMass: (25 + Math.sqrt(621)) / 2, planetMass: 1, result: false},
      {description: "Should fail with solar mass 10 and planet mass 1", solarMass: 10, planetMass: 1, result: false},
      {description: "Should fail with solar mass 1 and planet mass 1", solarMass: 1, planetMass: 1, result: false}
    ];

    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const result = service.lagrangeStability(parameter.solarMass, parameter.planetMass);
        expect(result).toBe(parameter.result);
      })
    })
  });
});
