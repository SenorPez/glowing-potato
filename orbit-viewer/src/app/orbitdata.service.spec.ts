import { TestBed } from '@angular/core/testing';

import { OrbitdataService } from './orbitdata.service';
import {Link, Planet} from "./api.service";
import {Vector3} from "three";

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

  describe("ephemerides", () => {
    const dummyLink: Link = {
      href: "/"
    };

    const omegaHydri3: Planet = {
      id: 159569841,
      name: "1 Omega Hydri 3",
      mass: 0.71235156,
      radius: 0.9022098,
      semimajorAxis: 0.93247896,
      eccentricity: 0.115,
      inclination: 0,
      longitudeOfAscendingNode: 0,
      argumentOfPeriapsis: 3.1809726,
      trueAnomalyAtEpoch: 4.653676,

      GM: 283935784992609,
      starGM: 1.3867924002240001e+20,
      lagrangePoints: {L1: false, L2: false, L3: false, L4: false, L5: false},

      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:calendars": dummyLink,
        "trident-api:planets": dummyLink
      }
    }

    const omegaHydri4: Planet = {
      id: 1008667220,
      name: "1 Omega Hydri 4",
      mass: 0.06909913,
      radius: 0.4282164,
      semimajorAxis: 1.5852143,
      eccentricity: 0.05,
      inclination: 0.032868735,
      longitudeOfAscendingNode: 3.5879867,
      argumentOfPeriapsis: 0.6304199,
      trueAnomalyAtEpoch: 2.9054375,

      GM: 27542181165232.988,
      starGM: 1.3867924002240001e+20,
      lagrangePoints: {L1: false, L2: false, L3: false, L4: false, L5: false},

      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:calendars": dummyLink,
        "trident-api:planets": dummyLink
      }
    }

    const parameters = [
      {description: "1 Omega Hydri 3 at epoch -100", planet: omegaHydri3, time: -8640000, position: new Vector3(153962795195.52725, -19978155934.90074, 0.0), velocity: new Vector3(4228.10592990422, 27829.32663385655, 0.0)},
      {description: "1 Omega Hydri 3 at epoch 0", planet: omegaHydri3, time: 0, position: new Vector3(2679144592.007491, 138561324414.57434, 0.0), velocity: new Vector3(-31590.91107946407, -3033.730289528269, 0.0)},
      {description: "1 Omega Hydri 3 at epoch 100", planet: omegaHydri3, time: 8640000, position: new Vector3(-94204526387.16089, -84343661283.09836, 0.0), velocity: new Vector3(21315.863343264646, -27294.78951134675, 0.0)},
      {description: "1 Omega Hydri 4 at epoch -100", planet: omegaHydri4, time: -8640000, position: new Vector3(242462782731.64142, 5829281881.332565, 3268886044.652998), velocity: new Vector3(493.7639685634058, 23621.49948081852, -693.5715821677534)},
      {description: "1 Omega Hydri 4 at epoch 0", planet: omegaHydri4, time: 0, position: new Vector3(165857354562.44604, 185209860374.5007, -3138709539.4643126), velocity: new Vector3(-16969.547861766354, 15564.752155681881, -702.5125448035453)},
      {description: "1 Omega Hydri 4 at epoch 100", planet: omegaHydri4, time: 8640000, position: new Vector3(-17547407271.258133, 246239664608.57016, -7552210137.703378), velocity: new Vector3(-23083.357177718353, -2303.364648217048, -259.3551307763122)},
    ];

    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const [positionResult, velocityResult]: [Vector3, Vector3] = service.ephemerides(parameter.planet, parameter.time);

        // Tolerance based on percentage difference.
        const tolerance = 1e-8;

        if (parameter.position.x !== 0) {
          expect(tolerance - Math.abs(positionResult.x / parameter.position.x - 1))
            .toBeGreaterThanOrEqual(0, [positionResult.x, parameter.position.x]);
        } else expect(positionResult.x).toEqual(0);

        if (parameter.position.y !== 0) {
          expect(tolerance - Math.abs(positionResult.y / parameter.position.y - 1))
            .toBeGreaterThanOrEqual(0, [positionResult.y, parameter.position.y]);
        } else expect(positionResult.y).toEqual(0);

        if (parameter.position.z !== 0) {
          expect(tolerance - Math.abs(positionResult.z / parameter.position.z - 1))
            .toBeGreaterThanOrEqual(0, [positionResult.z, parameter.position.z]);
        } else expect(positionResult.z).toEqual(0);

        if (parameter.velocity.x !== 0) {
          expect(tolerance - Math.abs(velocityResult.x / parameter.velocity.x - 1))
            .toBeGreaterThanOrEqual(0, [velocityResult.x, parameter.velocity.x]);
        } else expect(velocityResult.x).toEqual(0);

        if (parameter.velocity.y !== 0) {
          expect(tolerance - Math.abs(velocityResult.y / parameter.velocity.y - 1))
            .toBeGreaterThanOrEqual(0, [velocityResult.y, parameter.velocity.y]);
        } else expect(velocityResult.y).toEqual(0);

        if (parameter.velocity.z !== 0) {
          expect(tolerance - Math.abs(velocityResult.z / parameter.velocity.z - 1))
            .toBeGreaterThanOrEqual(0, [velocityResult.z, parameter.velocity.z]);
        } else expect(velocityResult.z).toEqual(0);
      });
    });
  });

  describe("orbitPeriod", () => {
    const dummyLink: Link = {
      href: "/"
    };

    const omegaHydri1: Planet = {
      id: 2035226060,
      name: "1 Omega Hydri 1",
      mass: 0.027045919,
      radius: 0.33969113,
      semimajorAxis: 0.30473167,
      eccentricity: 0.115,
      inclination: 0.001120502,
      longitudeOfAscendingNode: 3.826163,
      argumentOfPeriapsis: 1.9260389,
      trueAnomalyAtEpoch: 4.129414,

      GM: 10780216782443.096,
      starGM: 1.3867924002240001e+20,
      lagrangePoints: {L1: false, L2: false, L3: false, L4: false, L5: false},

      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:calendars": dummyLink,
        "trident-api:planets": dummyLink
      }
    }

    const omegaHydri2: Planet = {
      id: -154475081,
      name: "1 Omega Hydri 2",
      mass: 0.2618094,
      radius: 0.7204918,
      semimajorAxis: 0.54851705,
      eccentricity: 0.03,
      inclination: 0.014861423,
      longitudeOfAscendingNode: 4.87442,
      argumentOfPeriapsis: 3.2554474,
      trueAnomalyAtEpoch: 1.2192137,

      GM: 283935784992609,
      starGM: 1.3867924002240001e+20,
      lagrangePoints: {L1: false, L2: false, L3: false, L4: false, L5: false},

      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:calendars": dummyLink,
        "trident-api:planets": dummyLink
      }
    }

    const omegaHydri3: Planet = {
      id: 159569841,
      name: "1 Omega Hydri 3",
      mass: 0.71235156,
      radius: 0.9022098,
      semimajorAxis: 0.93247896,
      eccentricity: 0.115,
      inclination: 0,
      longitudeOfAscendingNode: 0,
      argumentOfPeriapsis: 3.1809726,
      trueAnomalyAtEpoch: 4.653676,

      GM: 283935784992609,
      starGM: 1.3867924002240001e+20,
      lagrangePoints: {L1: false, L2: false, L3: false, L4: false, L5: false},

      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:calendars": dummyLink,
        "trident-api:planets": dummyLink
      }
    }

    const omegaHydri4: Planet = {
      id: 1008667220,
      name: "1 Omega Hydri 4",
      mass: 0.06909913,
      radius: 0.4282164,
      semimajorAxis: 1.5852143,
      eccentricity: 0.05,
      inclination: 0.032868735,
      longitudeOfAscendingNode: 3.5879867,
      argumentOfPeriapsis: 0.6304199,
      trueAnomalyAtEpoch: 2.9054375,

      GM: 27542181165232.988,
      starGM: 1.3867924002240001e+20,
      lagrangePoints: {L1: false, L2: false, L3: false, L4: false, L5: false},

      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:calendars": dummyLink,
        "trident-api:planets": dummyLink
      }
    }

    const parameters = [
      {description: "1 Omega Hydri 1 at 5193242.83", planet: omegaHydri1, time: 5193242.83},
      {description: "1 Omega Hydri 2 at 12541428.68", planet: omegaHydri2, time: 12541428.68},
      {description: "1 Omega Hydri 3 at 27798436.97", planet: omegaHydri3, time: 27798436.97},
      {description: "1 Omega Hydri 4 at 61616097.55", planet: omegaHydri4, time: 61616097.55},
    ];

    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const period: number = service.orbitalPeriod(parameter.planet);
        expect(period).toBeCloseTo(parameter.time, 2);
      });
    });
  });
});
