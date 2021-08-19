import { TestBed } from '@angular/core/testing';

import { OrbitdataService } from './orbitdata.service';
import {Link, Planet} from "./api.service";
import {Vector3} from "three";

// Solutions source:
// * lagrangeStability: Mathematical test against constant
// * ephemerides: PyKep using data from Trident API
// * orbitPeriod: Mathematical; PyKep doesn't use M1 + M2 so numbers are off by a few seconds
// * lagrangePoint: Mathematical using rotation matrices
// * propagate: PyKep using data from Trident API
// * transfer: PyKep using data from Trident API

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

  describe("langrangePoint", () => {
    const dummyLink: Link = {
      href: "/"
    };

    const roundWorld: Planet = {
      id: 0,
      name: "ROUNDWORLD",
      mass: 1,
      radius: 1,
      semimajorAxis: 1,
      eccentricity: 0,
      inclination: 0,
      longitudeOfAscendingNode: 0,
      argumentOfPeriapsis: 0,
      trueAnomalyAtEpoch: 0,

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
      {description: "L4 point for perfect circle orbit", planet: roundWorld, time: 0, L4: true, position: new Vector3(74798935350.00003, 129555556378.25974, 0), velocity: new Vector3(-26367.785202492498, 15223.44788459328, 0)},
      {description: "L5 point for perfect circle orbit", planet: roundWorld, time: 0, L4: false, position: new Vector3(74798935350.00003, -129555556378.25974, 0), velocity: new Vector3(26367.785202492498, 15223.44788459328, 0)},
      {description: "L4 point for 1 Omega Hydri 4", planet: omegaHydri4, time: 0, L4: true, position: new Vector3(-77461706716.23093, 236125466219.01385, -8102724410.256062), velocity: new Vector3(-21974.998417084316, -6914.377226360811, -106.86573344549893)},
      {description: "L5 point for 1 Omega Hydri 4", planet: omegaHydri4, time: 0, L4: false, position: new Vector3(243319061288.6544, -50915605833.36989, 4964014870.602882), velocity: new Vector3(5005.450555828337, 22479.129381574417, -595.6468113369131)},
    ];

    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const [positionResult, velocityResult]: [Vector3, Vector3] = service.lagrangePoint(parameter.planet, parameter.time, parameter.L4);

        // Tolerance based on percentage difference.
        const tolerance = 1e-8;

        if (parameter.position.x !== 0) {
          expect(tolerance - Math.abs(positionResult.x / parameter.position.x - 1))
            .toBeGreaterThanOrEqual(0, "position x: " + [positionResult.x, parameter.position.x]);
        } else expect(positionResult.x).toEqual(0);

        if (parameter.position.y !== 0) {
          expect(tolerance - Math.abs(positionResult.y / parameter.position.y - 1))
            .toBeGreaterThanOrEqual(0, "position y: " + [positionResult.y, parameter.position.y]);
        } else expect(positionResult.y).toEqual(0);

        if (parameter.position.z !== 0) {
          expect(tolerance - Math.abs(positionResult.z / parameter.position.z - 1))
            .toBeGreaterThanOrEqual(0, "position z: " + [positionResult.z, parameter.position.z]);
        } else expect(positionResult.z).toEqual(0);

        if (parameter.velocity.x !== 0) {
          expect(tolerance - Math.abs(velocityResult.x / parameter.velocity.x - 1))
            .toBeGreaterThanOrEqual(0, "velocity x: " + [velocityResult.x, parameter.velocity.x]);
        } else expect(velocityResult.x).toEqual(0);

        if (parameter.velocity.y !== 0) {
          expect(tolerance - Math.abs(velocityResult.y / parameter.velocity.y - 1))
            .toBeGreaterThanOrEqual(0, "velocity y: " + [velocityResult.y, parameter.velocity.y]);
        } else expect(velocityResult.y).toEqual(0);

        if (parameter.velocity.z !== 0) {
          expect(tolerance - Math.abs(velocityResult.z / parameter.velocity.z - 1))
            .toBeGreaterThanOrEqual(0, "velocity z: " + [velocityResult.z, parameter.velocity.z]);
        } else expect(velocityResult.z).toEqual(0);
      })
    })
  })

  describe("propagate", () => {
    const parameters = [
      {description: "1 Omega Hydri 2, propagate 0+1 day", r0: new Vector3(-80903818304.70007, 6137945876.327766, -1171967768.6268415), v0: new Vector3(-4296.338710033931, -41346.68722826516, -162.15353444389586), mu: 1.3867924002240001e+20, time: 86400, position: new Vector3(-81196633524.24818, 2560805425.84375, -1184839521.4373617), velocity: new Vector3(-2481.8259650035943, -41444.08389891267, -135.77207092241997)},
      {description: "1 Omega Hydri 2, propagate 0+10 day", r0: new Vector3(-80903818304.70007, 6137945876.327766, -1171967768.6268415), v0: new Vector3(-4296.338710033931, -41346.68722826516, -162.15353444389586), mu: 1.3867924002240001e+20, time: 864000, position: new Vector3(-76881863249.70337, -29042365359.091934, -1197324970.7541199), velocity: new Vector3(13345.251440907401, -38811.46698589046, 102.68915008860694)},
      {description: "1 Omega Hydri 2, propagate 0+100 day", r0: new Vector3(-80903818304.70007, 6137945876.327766, -1171967768.6268415), v0: new Vector3(-4296.338710033931, -41346.68722826516, -162.15353444389586), mu: 1.3867924002240001e+20, time: 8640000, position: new Vector3(42628748895.083786, 68122822826.31849, 788607518.3740531), velocity: new Vector3(-36047.48697278536, 21483.60017222841, -477.2284004498464)},
      {description: "1 Omega Hydri 2, propagate 100+1 day", r0: new Vector3(42628748895.08386, 68122822826.31827, 788607518.3740537), v0: new Vector3(-36047.4869727854, 21483.60017222858, -477.2284004498467), mu: 1.3867924002240001e+20, time: 86400, position: new Vector3(39472736671.73497, 69910397310.18053, 746601631.5786998), velocity: new Vector3(-36996.64817930602, 19887.782259780986, -494.9767850971748)},
      {description: "1 Omega Hydri 2, propagate 100+10 day", r0: new Vector3(42628748895.08386, 68122822826.31827, 788607518.3740537), v0: new Vector3(-36047.4869727854, 21483.60017222858, -477.2284004498467), mu: 1.3867924002240001e+20, time: 864000, position: new Vector3(8304862886.419426, 79327039892.26137, 312014107.28799677), velocity: new Vector3(-42087.62222642359, 3948.178146694767, -607.8682272114236)},
      {description: "1 Omega Hydri 2, propagate 100+100 day", r0: new Vector3(42628748895.08386, 68122822826.31827, 788607518.3740537), v0: new Vector3(-36047.4869727854, 21483.60017222858, -477.2284004498467), mu: 1.3867924002240001e+20, time: 8640000, position: new Vector3(50986443796.61917, -67179625592.2766, 586786904.8434366), velocity: new Vector3(31569.296656377777, 24529.30510092464, 521.8665490001245)},
    ];

    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const [positionResult, velocityResult]: [Vector3, Vector3] = service.propagate(parameter.r0, parameter.v0, parameter.mu, parameter.time);

        // Tolerance based on percentage difference.
        const tolerance = 1e-8;

        if (parameter.position.x !== 0) {
          expect(tolerance - Math.abs(positionResult.x / parameter.position.x - 1))
            .toBeGreaterThanOrEqual(0, "position x: " + [positionResult.x, parameter.position.x]);
        } else expect(positionResult.x).toEqual(0);

        if (parameter.position.y !== 0) {
          expect(tolerance - Math.abs(positionResult.y / parameter.position.y - 1))
            .toBeGreaterThanOrEqual(0, "position y: " + [positionResult.y, parameter.position.y]);
        } else expect(positionResult.y).toEqual(0);

        if (parameter.position.z !== 0) {
          expect(tolerance - Math.abs(positionResult.z / parameter.position.z - 1))
            .toBeGreaterThanOrEqual(0, "position z: " + [positionResult.z, parameter.position.z]);
        } else expect(positionResult.z).toEqual(0);

        if (parameter.velocity.x !== 0) {
          expect(tolerance - Math.abs(velocityResult.x / parameter.velocity.x - 1))
            .toBeGreaterThanOrEqual(0, "velocity x: " + [velocityResult.x, parameter.velocity.x]);
        } else expect(velocityResult.x).toEqual(0);

        if (parameter.velocity.y !== 0) {
          expect(tolerance - Math.abs(velocityResult.y / parameter.velocity.y - 1))
            .toBeGreaterThanOrEqual(0, "velocity y: " + [velocityResult.y, parameter.velocity.y]);
        } else expect(velocityResult.y).toEqual(0);

        if (parameter.velocity.z !== 0) {
          expect(tolerance - Math.abs(velocityResult.z / parameter.velocity.z - 1))
            .toBeGreaterThanOrEqual(0, "velocity z: " + [velocityResult.z, parameter.velocity.z]);
        } else expect(velocityResult.z).toEqual(0);
      });
    });
  });

  describe("transfer", () => {
    const parameters = [
      {description: "1OH1 to 1OH2 in 69 days", r1: new Vector3(-43099846467.713509, -21184300229.783638, -12149029.904668095), r2: new Vector3(65105257227.044586, -53149988555.063141, 827517816.40368068), tof: 69 * 86400, mu: 1.386792400224e+20, v1: new Vector3(-6631.4941884090513, -60198.685951011015, 539.2121074882856), v2: new Vector3(4180.3992748278797, 34281.139183450432, -305.06246705425156)},
      {description: "1OH1 to 1OH2 in 100 days", r1: new Vector3(-43099846467.713509, -21184300229.783638, -12149029.904668095), r2: new Vector3(36760504742.277031, -76060853818.371521, 356828852.02859867), tof: 100 * 86400, mu: 1.386792400224e+20, v1: new Vector3(-24851.801387277297, -57532.171536393536, 159.79587093243714), v2: new Vector3(7755.8827625773647, 37084.383983690015, -120.28072165933895)},
      {description: "1OH1 to 1OH2 in 117 days", r1: new Vector3(-43099846467.713509, -21184300229.783638, -12149029.904668095), r2: new Vector3(-56936444459.269653, -60757228200.18483, -980810321.26770115), tof: 117 * 86400, mu: 1.386792400224e+20, v1: new Vector3(-51696.855908278805, -36631.535472830306, -344.92199497426554), v2: new Vector3(31038.883054668775, 24627.190473330134, 284.61978327276211)}
    ];

    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const [v1, v2]: [Vector3, Vector3] = service.transfer(parameter.r1, parameter.r2, parameter.tof, parameter.mu);

        // Tolerance based on percentage difference.
        const tolerance = 1e-8;

        if (parameter.v1.x !== 0) {
          expect(tolerance - Math.abs(v1.x / parameter.v1.x - 1))
            .toBeGreaterThanOrEqual(0, "v1 x: " + [parameter.r1.x, parameter.r2.x, v1.x, parameter.v1.x]);
        } else expect(v1.x).toEqual(0);

        if (parameter.v1.y !== 0) {
          expect(tolerance - Math.abs(v1.y / parameter.v1.y - 1))
            .toBeGreaterThanOrEqual(0, "v1 y: " + [parameter.r1.y, parameter.r2.y, v1.y, parameter.v1.y]);
        } else expect(v1.y).toEqual(0);

        if (parameter.v1.z !== 0) {
          expect(tolerance - Math.abs(v1.z / parameter.v1.z - 1))
            .toBeGreaterThanOrEqual(0, "v1 z: " + [parameter.r1.z, parameter.r2.z, v1.z, parameter.v1.z]);
        } else expect(v1.z).toEqual(0);

        if (parameter.v2.x !== 0) {
          expect(tolerance - Math.abs(v2.x / parameter.v2.x - 1))
            .toBeGreaterThanOrEqual(0, "v2 x: " + [parameter.r1.x, parameter.r2.x, v2.x, parameter.v2.x]);
        } else expect(v2.x).toEqual(0);

        if (parameter.v2.y !== 0) {
          expect(tolerance - Math.abs(v2.y / parameter.v2.y - 1))
            .toBeGreaterThanOrEqual(0, "v2 y: " + [parameter.r1.y, parameter.r2.y, v2.y, parameter.v2.y]);
        } else expect(v2.y).toEqual(0);

        if (parameter.v2.z !== 0) {
          expect(tolerance - Math.abs(v2.z / parameter.v2.z - 1))
            .toBeGreaterThanOrEqual(0, "v2 z: " + [parameter.r1.z, parameter.r2.z, v2.z, parameter.v2.z]);
        } else expect(v2.z).toEqual(0);
      });
    });
  });
});
