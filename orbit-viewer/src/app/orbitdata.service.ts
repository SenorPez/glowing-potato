import {Injectable} from '@angular/core';
import {Vector3} from 'three';

import {throwError} from "rxjs";
import {Planet} from "./api.service";
import {min} from "rxjs/operators";
import {mixinDisabled} from "@angular/material/core";

@Injectable({
  providedIn: 'root'
})
export class OrbitdataService {
  // TODO: Add AU to API
  private AU: number = 149597870691;

  ephemerides(planet: Planet, time: number) {
    const semimajorAxis = planet.semimajorAxis * this.AU;
    const eccentricity = planet.eccentricity;
    const inclination = planet.inclination;
    const longitudeOfAscendingNode = planet.longitudeOfAscendingNode;
    const argumentOfPeriapsis = planet.argumentOfPeriapsis;
    const trueAnomalyAtEpoch = planet.trueAnomalyAtEpoch;

    const meanMotion = Math.sqrt(planet.starGM / Math.pow(semimajorAxis, 3));
    const meanAnomaly = OrbitdataService.trueToMean(trueAnomalyAtEpoch, eccentricity) + meanMotion * time;
    const eccentricAnomaly = OrbitdataService.meanToEccentric(meanAnomaly, eccentricity);

    const semiminorAxis = semimajorAxis * Math.sqrt(1 - eccentricity * eccentricity);
    const xper = semimajorAxis * (Math.cos(eccentricAnomaly) - eccentricity);
    const yper = semiminorAxis * Math.sin(eccentricAnomaly);
    const xdotper = -(semimajorAxis * meanMotion * Math.sin(eccentricAnomaly)) / (1 - eccentricity * Math.cos(eccentricAnomaly));
    const ydotper = (semiminorAxis * meanMotion * Math.cos(eccentricAnomaly)) / (1 - eccentricity * Math.cos(eccentricAnomaly));

    const cosLoAN = Math.cos(longitudeOfAscendingNode);
    const cosAoP = Math.cos(argumentOfPeriapsis);
    const cosi = Math.cos(inclination);
    const sinLoAN = Math.sin(longitudeOfAscendingNode);
    const sinAoP = Math.sin(argumentOfPeriapsis);
    const sini = Math.sin(inclination);

    const rotationMatrix: number[][] = [[], [], []];
    rotationMatrix[0][0] = cosLoAN * cosAoP - sinLoAN * sinAoP * cosi;
    rotationMatrix[0][1] = -cosLoAN * sinAoP - sinLoAN * cosAoP * cosi;
    rotationMatrix[0][2] = sinLoAN * sini;
    rotationMatrix[1][0] = sinLoAN * cosAoP + cosLoAN * sinAoP * cosi;
    rotationMatrix[1][1] = -sinLoAN * sinAoP + cosLoAN * cosAoP * cosi;
    rotationMatrix[1][2] = -cosLoAN * sini;
    rotationMatrix[2][0] = sinAoP * sini;
    rotationMatrix[2][1] = cosAoP * sini;
    rotationMatrix[2][2] = cosi;

    const r: number[] = [];
    const v: number[] = [];
    const perr: number[] = [xper, yper, 0];
    const perv: number[] = [xdotper, ydotper, 0];

    for (let j = 0; j < 3; j++) {
      r[j] = 0;
      v[j] = 0;
      for (let k = 0; k < 3; k++) {
        r[j] += rotationMatrix[j][k] * perr[k];
        v[j] += rotationMatrix[j][k] * perv[k];
      }
    }

    const position: Vector3 = new Vector3(r[0], r[1], r[2]);
    const velocity: Vector3 = new Vector3(v[0], v[1], v[2]);

    return [position, velocity];
  }

  transfer_new(r1: Vector3, r2: Vector3, tof: number, mu: number) {
    // r1 = new Vector3(0.5, 0.6, 0.7);
    // r2 = new Vector3(0, 1.0, 0.0);
    // tof = 0.9667663
    // mu = 1;

    const m_r1 = r1.length();
    const m_r2 = r2.length();

    const chord = Math.sqrt((r2.x - r1.x) ** 2 + (r2.y - r1.y) ** 2 + (r2.z - r1.z) ** 2);
    const semiperimeter = (chord + m_r1 + m_r2) / 2.0;
    const lambda = Math.sqrt(1 - chord / semiperimeter);
    const ndToF = tof * Math.sqrt(2 * mu / Math.pow(semiperimeter, 3));
    // console.log(chord, semiperimeter, lambda, ndToF);

    const cosDeltaNu = r1.dot(r2) / (m_r1 * m_r2);
    let deltaNu = Math.acos(cosDeltaNu);
    // deltaNu = 2 * Math.PI - deltaNu;
    let DM = Math.PI - deltaNu < 0 ? -1 : 1;

    const A = DM * Math.sqrt(m_r1 * m_r2 * (1 + cosDeltaNu));

    // Initial Guess
    let z = 0

    const factorial: (n: number) => (number) = (n: number) => {
      if (n < 0) {
        return -1;
      } else if (n == 0) {
        return 1;
      } else {
        return n * factorial(n - 1);
      }
    }

    const expansionC = (z: number) => {
      let sum = 1 / factorial(2);
      for (let i = 1; i < 101; i++) {
        const sign = i % 2 ? -1 : 1;
        sum += sign * Math.pow(z, i) / factorial(2 + i * 2);
      }
      return sum;
    }

    const expansionDC = (z: number) => {
      return z === 0 ? 1 / factorial(4) : (1 / (2 * z)) * (1 - z * expansionS(z) - 2 * expansionC(z));

      // TODO: Figure out what I'm doing wrong.
      // let sum = 1 / factorial(4);
      // for (let i = 1; i < 101; i++) {
      //   const sign = i % 2 ? 1 : -1;
      //   sum += sign * 2 * Math.pow(z, i) / factorial(4 + i * 2);
      // }
      // return sum;
    }

    const expansionS: (z: number) => number = (z: number) => {
      let sum = 1 / factorial(3);
      for (let i = 1; i < 101; i++) {
        const sign = i % 2 ? -1 : 1;
        sum += sign * Math.pow(z, i) / factorial(3 + i * 2);
      }
      return sum;
    }

    const expansionDS = (z: number) => {
      return z === 0 ? 1 / factorial(5) : (1 / (2 * z)) * (expansionC(z) - 3 * expansionS(z));

      // TODO: Figure out what I'm doing wrong.
      // let sum = 1 / factorial(5);
      // for (let i = 1; i < 101; i++) {
      //   const sign = i % 2 ? 1 : -1;
      //   console.log(sign, i, 1 + i, 5 + i * 2);
      //   sum += sign * (1 + i) * Math.pow(z, i) / factorial(5 + i * 2);
      // }
      // return sum;
    }

    const calc = (z: number) => {
      const C = expansionC(z);
      const S = expansionS(z);

      const y = m_r1 + m_r2 - A * ((1 - z * S) / Math.sqrt(C));
      const x = Math.sqrt(y / C);

      const t = (Math.pow(x, 3) * S + A * Math.sqrt(y)) / Math.sqrt(mu);

      const dC = expansionDC(z);
      const dS = expansionDS(z);

      const dtdz = (x ** 3 * (dS - ((3 * S * dC) / (2 * C))) + (A / 8) * (((3 * S * Math.sqrt(y)) / C) + (A / x))) / Math.sqrt(mu);

      return [t, dtdz, y];
    };

    // console.log(Math.sqrt(2.87869), tof);
    let [t, dtdz, y] = calc(z);
    while (Math.abs(t - tof) > 10e-4) {
      z = z + (tof - t) / dtdz;
      [t, dtdz, y] = calc(z);
    }

    const f = 1 - y / m_r1;
    const g = A * Math.sqrt(y / mu);
    const gdot = 1 - y / m_r2;

    const v1: Vector3 = new Vector3(
      (r2.x - f * r1.x) / g,
      (r2.y - f * r1.y) / g,
      (r2.z - f * r1.z) / g
    );
    const v2: Vector3 = new Vector3(
      (gdot * r2.x - r1.x) / g,
      (gdot * r2.y - r1.y) / g,
      (gdot * r2.z - r1.z) / g
    )

    return [v1, v2];
  }

  transfer(r1: Vector3, r2: Vector3, tof: number, mu: number) {
    const m_r1 = r1.length();
    const m_r2 = r2.length();

    const chord = Math.sqrt((r2.x - r1.x) ** 2 + (r2.y - r1.y) ** 2 + (r2.z - r1.z) ** 2);
    const semiperimeter = (chord + m_r1 + m_r2) / 2.0;
    const lambda = Math.sqrt(1 - chord / semiperimeter);
    const ndToF = tof * Math.sqrt(2 * mu / Math.pow(semiperimeter, 3));

    console.log(mu, r1, r2, tof, chord, semiperimeter, lambda, ndToF);

    const cosDeltaNu = r1.dot(r2) / (m_r1 * m_r2);
    let deltaNu = Math.acos(cosDeltaNu);

    const k = m_r1 * m_r2 * (1 - cosDeltaNu);
    const l = m_r1 + m_r2
    const m = m_r1 * m_r2 * (1 + cosDeltaNu);

    const pMin = k / (l + Math.sqrt(2 * m));
    const pMax = k / (l - Math.sqrt(2 * m));

    // const pMin = 40700000000
    // const pMax = 40800000000

    const tofSolver = (p: number) => {
      const a = m * k * p / ((2 * m - l * l) * p * p + 2 * k * l * p - k * k);
      // const a = 110702928630.34;
      // p = (-k * Math.sqrt(m) * Math.sqrt(8 * a * a - 4 * a * l + m) + 2 * a * k * l - k * m) / (2 * (a * l * l - 2 * a * m));
      const f = 1 - m_r2 / p * (1 - cosDeltaNu);
      const g = m_r1 * m_r2 * Math.sin(deltaNu) / Math.sqrt(mu * p);
      const fdot = Math.sqrt(mu / p) * Math.tan(deltaNu / 2) * ((1 - cosDeltaNu) / p - 1 / m_r1 - 1 / m_r2);
      const gdot = 1 - m_r1 / p * (1 - cosDeltaNu);

      let t;
      if (a >= 0) { // Ellipse
        const cosDeltaE = 1 - m_r1 / a * (1 - f);
        let sinDeltaE = -m_r1 * m_r2 * fdot / Math.sqrt(mu * a);
        let deltaE = Math.atan2(sinDeltaE, cosDeltaE);

        t = g + Math.sqrt(Math.pow(a, 3) / mu) * (deltaE - sinDeltaE);
        console.log(deltaE, Math.abs(deltaE - 2.87869));
        // console.log(tof / (g + Math.sqrt(Math.pow(a, 3) / mu)));
        // console.log(pMax > p, deltaE, Math.abs(tof - t), "de");
      } else { // Hyperbola
        const coshDeltaF = 1 - (m_r1 / a) * (1 - f);
        let deltaF = Math.acosh(coshDeltaF);
        t = g + Math.sqrt(Math.pow(-a, 3) / mu) * (Math.sinh(deltaF) - deltaF);
        // console.log(pMax > p, deltaF, Math.abs(tof - t), "df");
      }

      const e = t - tof;
      return {
        p: p,
        e: e,
        t: t,
        f: f,
        g: g,
        fdot: fdot,
        gdot: gdot,
        a: a
      };
    };

    const pvals: number[] = [];
    const evals: number[] = [];
    const tvals: number[] = [];

    // Initial two trials.
    const first = tofSolver(0.7 * pMin + 0.3 * pMax);
    [pvals[0], evals[0], tvals[0]] = [first.p, first.e, first.t];
    const second = tofSolver(0.3 * pMin + 0.7 * pMax);
    [pvals[1], evals[1], tvals[1]] = [second.p, second.e, second.t];

    let iter: number = 1;
    let result;

    // while (Math.abs(evals[iter]) > 1e-5 && iter < 100) {
    //   let pNext = pvals[iter] + ((tof - tvals[iter]) * (pvals[iter] - pvals[iter - 1])) / (tvals[iter] - tvals[iter - 1]);
    //   result = tofSolver(pNext);
    //
    //   iter++;
    //   pvals[iter] = result.p;
    //   evals[iter] = result.e;
    //   tvals[iter] = result.t;
    // }
    //
    // for (let b = pMin; b < pMax; b = b + ((pMax - pMin) / 100)) {
    //   let t = tofSolver(b).t;
    //   console.log(b, tof, t, Math.abs(tof - t));
    // }
    const target = 40734602439;
    let q = tofSolver(target).t;
    // console.log(target, tof, q, Math.abs(tof - q));

    result = tofSolver(target);

    if (result !== undefined) {
      const {f, g, fdot, gdot} = result;
      const v1 = new Vector3(
        (r2.x - f * r1.x) / g,
        (r2.y - f * r1.y) / g,
        (r2.z - f * r1.z) / g
      );
      const v2 = new Vector3(
        fdot * r1.x + gdot * v1.x,
        fdot * r1.y + gdot * v1.y,
        fdot * r1.z + gdot * v1.z
      );

      console.log(v1, v2);

      return [v1, v2];
    }
    console.log("ERROR");
    return [new Vector3(), new Vector3()];
  }

  // // TODO: Add AU to API
  // private AU: number = 149597870700;
  //
  // private flaskApp: string;
  // private api: string;
  //
  // constructor() {
  //   // this.flaskApp = 'https://www.senorpez.com/tw';
  //   this.flaskApp = 'http://127.0.0.1:5000'
  //   this.api = "https://www.trident.senorpez.com/"
  // }
  //
  // orbitalPeriod(planet: Planet) {
  //   return 2 * Math.PI * Math.sqrt(Math.pow(planet.semimajorAxis * this.AU, 3) / (planet.starGM + planet.GM));
  // }
  //
  // getPlanetIds(system_id: number, star_id: number) {
  //   return fetch(this.api + `/systems/${system_id}/stars/${star_id}/planets`, {
  //     method: 'GET',
  //     mode: "no-cors",
  //     headers: {
  //       'Accept': 'application/json'
  //     }
  //   })
  //     .then(response => response.json())
  //     .then(data => data._embedded["trident-api:planet"].map((item: any) => item.planet_id));
  // }
  //
  // getLambert(min_delta_v: boolean, date: string, system_id: number, star_id: number, origin_planet_id: number, target_planet_id: number): Promise<[Vector3[], Vector3, Vector3, Vector3, number, number, number]> {
  //   const transfer_type = min_delta_v ? '/orbit/dvlambert' : '/orbit/ftlambert';
  //
  //   return fetch(this.flaskApp + transfer_type, {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json'
  //     },
  //     body: JSON.stringify({
  //       'system_id': system_id,
  //       'star_id': star_id,
  //       'origin_planet_id': origin_planet_id,
  //       'target_planet_id': target_planet_id,
  //       'launch_date': date
  //     })
  //   })
  //     .then(response => response.json())
  //     .then(data => {
  //       const pathData: [Vector3, Vector3, Vector3] = data.x.map(function (element: number, index: number) {
  //         return new Vector3(element, data.y[index], data.z[index]);
  //       });
  //       const r1: Vector3 = new Vector3(data.r1[0], data.r1[1], data.r1[2]);
  //       const r2: Vector3 = new Vector3(data.r2[0], data.r2[1], data.r2[2])
  //       const v1: Vector3 = new Vector3(data.v1[0], data.v1[1], data.v1[2]);
  //       return [pathData, r1, r2, v1, data.mu, data.flight_time, data.dv];
  //     });
  // }
  //
  // getPath(system_id: number, star_id: number, planet_id: number): Promise<Vector3[]> {
  //   return fetch(this.flaskApp + '/orbit/path', {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json'
  //     },
  //     body: JSON.stringify({
  //       'system_id': system_id,
  //       'star_id': star_id,
  //       'planet_id': planet_id
  //     })
  //   })
  //     .then(response => response.json())
  //     .then(data => {
  //       return data.x.map(function (element: number, index: number) {
  //         return new Vector3(element, data.y[index], data.z[index]);
  //       });
  //     })
  // }
  //
  // // getPlanet(system_id: number, star_id: number, planet_id:number): Promise<Planet> {
  // //   return fetch(this.flaskApp + '/orbit/planet', {
  // //     method: 'POST',
  // //     headers: {
  // //       'Content-Type': 'application/json'
  // //     },
  // //     body: JSON.stringify({
  // //       'system_id': system_id,
  // //       'star_id': star_id,
  // //       'planet_id': planet_id
  // //     })
  // //   })
  // //     .then(response => response.json())
  // //     .then(data => {
  // //       const planet: Planet = {
  // //         name: data['name'],
  // //         mass: data['mass'],
  // //         radius: data['radius'],
  // //         semimajorAxis: data['semimajorAxis'],
  // //         eccentricity: data['eccentricity'],
  // //         inclination: data['inclination'],
  // //         longitudeOfAscendingNode: data['longitudeOfAscendingNode'],
  // //         argumentOfPeriapsis: data['argumentOfPeriapsis'],
  // //         trueAnomalyAtEpoch: data['trueAnomalyAtEpoch'],
  // //         starGM: data['starGM'],
  // //         GM: data['GM']
  // //       };
  // //       return planet;
  // //     }
  // //   )
  // // }
  //
  // getRpln(): Promise<number> {
  //   return fetch(this.flaskApp + '/orbit/Rpln', {
  //     method: 'GET',
  //     headers: {
  //       'Content-Type': 'application/json'
  //     }
  //   })
  //     .then(response => response.json())
  //     .then(data => data.Rpln);
  // }
  //
  // getEarthPosition(t0: number): Promise<Vector3> {
  //   return fetch(this.flaskApp + '/orbit/earthposition', {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json'
  //     },
  //     body: JSON.stringify({
  //       't0': t0
  //     })
  //   })
  //     .then(response => response.json())
  //     .then(data => new Vector3(data.x, data.y, data.z))
  // }
  //
  // getEarthPath(t0: number): Promise<Vector3[]> {
  //   return fetch(this.flaskApp + '/orbit/earthpath', {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json'
  //     },
  //     body: JSON.stringify({
  //       't0': t0
  //     })
  //   })
  //     .then(response => response.json())
  //     .then(data => {
  //       return data.x.map(function (element: number, index: number) {
  //         return new Vector3(element, data.y[index], data.z[index]);
  //       });
  //     })
  // }
  //
  // private getMeanMotion(planet: Planet): number {
  //   return Math.sqrt((planet.starGM + planet.GM) / Math.pow(planet.semimajorAxis * this.AU, 3));
  // }
  //
  private static trueToEccentric(trueAnomaly: number, eccentricity: number): number {
    return Math.atan2(
      Math.sqrt(1 - Math.pow(eccentricity, 2)) * Math.sin(trueAnomaly),
      eccentricity + Math.cos(trueAnomaly)
    ) % (2 * Math.PI);
  }

  private static eccentricToMean(eccentricAnomaly: number, eccentricity: number): number {
    return eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly);
  }

  private static trueToMean(trueAnomaly: number, eccentricity: number): number {
    return OrbitdataService.eccentricToMean(OrbitdataService.trueToEccentric(trueAnomaly, eccentricity), eccentricity);
  }

  private static meanToEccentric(meanAnomaly: number, eccentricity: number): number {
    const maxIterations: number = 30;
    const delta = Math.pow(10, -10);

    let E = meanAnomaly;
    let F = E - eccentricity * Math.sin(meanAnomaly) - meanAnomaly;
    let i = 0;
    while ((Math.abs(F) > delta) && (i < maxIterations)) {
      i += 1;
      E = E - F / (1 - eccentricity * Math.cos(E));
      F = E - eccentricity * Math.sin(E) - meanAnomaly;
    }

    E = E % (2 * Math.PI);
    return E;
  }
  //
  // ephemeris(planet: Planet, time: number): [Vector3, Vector3] {
  //   const meanMotion = this.getMeanMotion(planet);
  //   const meanAnomaly = OrbitdataService.trueToMean(planet.trueAnomalyAtEpoch, planet.eccentricity) + meanMotion * time;
  //   const eccentricAnomaly = OrbitdataService.meanToEccentric(meanAnomaly, planet.eccentricity);
  //
  //   const semiminorAxis = planet.semimajorAxis * this.AU * Math.sqrt(1 - planet.eccentricity * planet.eccentricity);
  //
  //   const xPer = planet.semimajorAxis * this.AU * (Math.cos(eccentricAnomaly) - planet.eccentricity);
  //   const yPer = semiminorAxis * Math.sin(eccentricAnomaly);
  //   const xDotPer = -(planet.semimajorAxis * this.AU * meanMotion * Math.sin(eccentricAnomaly)) / (1 - planet.eccentricity * Math.cos(eccentricAnomaly));
  //   const yDotPer = (semiminorAxis * meanMotion * Math.cos(eccentricAnomaly)) / (1 - planet.eccentricity * Math.cos(eccentricAnomaly));
  //
  //   const cosLoAN = Math.cos(planet.longitudeOfAscendingNode);
  //   const cosAoP = Math.cos(planet.argumentOfPeriapsis);
  //   const cosInc = Math.cos(planet.inclination);
  //   const sinLoAN = Math.sin(planet.longitudeOfAscendingNode);
  //   const sinAoP = Math.sin(planet.argumentOfPeriapsis);
  //   const sinInc = Math.sin(planet.inclination);
  //
  //   const rotationMatrix: number[][] = [[], [], []];
  //   rotationMatrix[0][0] = cosLoAN * cosAoP - sinLoAN * sinAoP * cosInc;
  //   rotationMatrix[0][1] = -cosLoAN * sinAoP - sinLoAN * cosAoP * cosInc;
  //   rotationMatrix[0][2] = sinLoAN * sinInc;
  //   rotationMatrix[1][0] = sinLoAN * cosAoP + cosLoAN * sinAoP * cosInc;
  //   rotationMatrix[1][1] = -sinLoAN * sinAoP + cosLoAN * cosAoP * cosInc;
  //   rotationMatrix[1][2] = -cosLoAN * sinInc;
  //   rotationMatrix[2][0] = sinAoP * sinInc;
  //   rotationMatrix[2][1] = cosAoP * sinInc;
  //   rotationMatrix[2][2] = cosInc;
  //
  //   const peri1: number[] = [xPer, yPer, 0.0];
  //   const peri2: number[] = [xDotPer, yDotPer, 0.0];
  //   const r: number[] = [];
  //   const v: number[] = [];
  //
  //   for (var j = 0; j < 3; j++) {
  //     r[j] = 0.0;
  //     v[j] = 0.0;
  //     for (var k = 0; k < 3; k++) {
  //       r[j] += rotationMatrix[j][k] * peri1[k];
  //       v[j] += rotationMatrix[j][k] * peri2[k];
  //     }
  //   }
  //   const position: Vector3 = new Vector3(r[0], r[1], r[2]);
  //   const velocity: Vector3 = new Vector3(v[0], v[1], v[2]);
  //   return [position, velocity];
  // }
  //
  // lambertSolver(r1: Vector3, r2: Vector3, tof: number, mu: number) {
  //   //
  //   // const r1 = new Vector3(0.473265, -0.899215);
  //   // const r2 = new Vector3(0.066842, 1.561256, 0.030948);
  //   // const mu = 3.964016e-14
  //   // const tof = 207
  //
  //   const m_r1: number = r1.length();
  //   const m_r2: number = r2.length();
  //
  //   let angularMomentum: Vector3 = new Vector3();
  //   angularMomentum.crossVectors(r1, r2);
  //
  //   const cosDeltaNu: number = r1.dot(r2) / (m_r1 * m_r2);
  //   const deltaNu: number = angularMomentum.z >= 0 ?
  //     Math.acos(cosDeltaNu) :
  //     2 * Math.PI - Math.acos(cosDeltaNu);
  //   // const deltaNu: number = 2 * Math.PI - Math.acos(cosDeltaNu);
  //
  //   const k: number = m_r1 * m_r2 * (1 - cosDeltaNu);
  //   const l: number = m_r1 + m_r2;
  //   const m: number = m_r1 * m_r2 * (1 + cosDeltaNu);
  //
  //   const pMin: number = k / (l + Math.sqrt(2 * m));
  //   const pMax: number = k / (l - Math.sqrt(2 * m));
  //
  //   const pvals: number[] = new Array(100);
  //   const tvals: number[] = new Array(100);
  //   const evals: number[] = new Array(100);
  //
  //   const tofSolver = (p: number) => {
  //     const a: number = m * k * p / ((2 * m - l * l) * p * p + 2 * k * l * p - k * k);
  //     const f: number = 1 - m_r2 / p * (1 - cosDeltaNu);
  //     const g: number = m_r1 * m_r2 * Math.sin(deltaNu) / Math.sqrt(p * mu);
  //     const dotf: number = Math.sqrt(mu / p) * ((1 - cosDeltaNu) / p - 1 / m_r1 - 1 / m_r2) * Math.tan(deltaNu / 2);
  //     const dotg: number = 1 - m_r1 / p * (1 - cosDeltaNu);
  //     const cosDeltaE: number = 1 - m_r1 / a * (1 - f);
  //     const sinDeltaE: number = -m_r1 * m_r2 * dotf / Math.sqrt(mu * a);
  //     let deltaE: number = Math.atan2(sinDeltaE, cosDeltaE);
  //     while (deltaE < 0) deltaE += 2 * Math.PI;
  //     const t: number = ((g + Math.sqrt(Math.pow(a, 3) / mu) * (deltaE - sinDeltaE)));
  //     const e: number = t - tof;
  //     return [p, e, t, f, g, dotf, dotg];
  //   }
  //
  //   // Populate initial two trials.
  //   [pvals[0], evals[0], tvals[0]] = tofSolver(0.7 * pMin + 0.3 * pMax);
  //   // [pvals[0], evals[0], tvals[0]] = tofSolver(1.2);
  //   [pvals[1], evals[1], tvals[1]] = tofSolver(0.3 * pMin + 0.7 * pMax);
  //   // [pvals[1], evals[1], tvals[1]] = tofSolver(1.3);
  //
  //   // Solve for ToF
  //   let iter: number = 1;
  //   let p: number = 0;
  //   let e: number = 0;
  //   let t: number = 0;
  //   let f: number = 0;
  //   let g: number = 0;
  //   let dotf: number = 0;
  //   let dotg: number = 0;
  //
  //   // console.log(pvals[iter], tof, tvals[iter], pvals[iter], pvals[iter - 1], tvals[iter], tvals[iter - 1]);
  //   // console.log(pvals[iter] + ((tof - tvals[iter]) * (pvals[iter] - pvals[iter - 1])) / (tvals[iter] - tvals[iter - 1]));
  //
  //   while (Math.abs(evals[iter]) > 1e-5 && iter < 100) {
  //     [p, e, t, f, g, dotf, dotg] = tofSolver(pvals[iter] + ((tof - tvals[iter]) * (pvals[iter] - pvals[iter - 1])) / (tvals[iter] - tvals[iter - 1]));
  //     // = tofSolver(pvals[iter] - tvals[iter] * (pvals[iter] - pvals[iter - 1]) / (evals[iter] - evals[iter - 1]));
  //     iter++;
  //     pvals[iter] = p;
  //     evals[iter] = e;
  //     tvals[iter] = t;
  //   }
  //
  //   const v1: Vector3 = new Vector3(
  //     (r2.x - f * r1.x) / g,
  //     (r2.y - f * r1.y) / g,
  //     (r2.z - f * r1.z) / g);
  //   const v2: Vector3 = new Vector3(
  //     dotf * r1.x + dotg * (r2.x - f * r1.x) / g,
  //     dotf * r1.y + dotg * (r2.y - f * r1.y) / g,
  //     dotf * r1.z + dotg * (r2.z - f * r1.z) / g);
  //
  //   // console.log(tof);
  //
  //   return [v1, v2];
  // }
  //
  // propagate(position: Vector3, velocity: Vector3, mu: number, time: number): [Vector3, Vector3] {
  //   // const R: number = Math.sqrt(position.x * position.x + position.y * position.y + position.z * position.z);
  //   // const V: number = Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
  //
  //   const R: number = position.length();
  //   const V: number = velocity.length();
  //   const energy: number = V * V / 2 - mu / R;
  //   const a: number = -mu / 2.0 / energy;
  //   const h: Vector3 = new Vector3();
  //   h.crossVectors(position, velocity);
  //   const p: number = Math.pow(h.length(), 2) / mu;
  //   const e: number = Math.sqrt(1 - p / a);
  //
  //   // const sigma0: number = (position.x * velocity.x + position.y * velocity.y + position.z * velocity.z) / Math.sqrt(mu);
  //   const sigma0: number = position.dot(velocity) / Math.sqrt(mu);
  //
  //   let F: number = 1;
  //   let G: number = 1;
  //   let Ft: number = 1;
  //   let Gt: number = 1;
  //
  //   if (a > 0) {
  //     const DM: number = Math.sqrt(mu / Math.pow(a, 3)) * time;
  //     const DE: number = OrbitdataService.meanToEccentric(DM, e);
  //     const r: number = a + (R - a) * Math.cos(DE) + sigma0 * Math.sqrt(a) * Math.sin(DE);
  //
  //     F = 1 - a / R * (1 - Math.cos(DE));
  //     G = a * sigma0 / Math.sqrt(mu) * (1 - Math.cos(DE)) + R * Math.sqrt(a / mu) * Math.sin(DE);
  //     Ft = -Math.sqrt(mu * a) / (r * R) * Math.sin(DE);
  //     Gt = 1 - a / r * (1 - Math.cos(DE));
  //
  //     // const DM: number = Math.sqrt(mu / Math.pow(a, 3)) * time;
  //     // let DE = DM;
  //     //
  //     // let iter: number = 0;
  //     // let err: number = 1.0;
  //     // let j: number = 0;
  //     // let k: number = 2 * Math.PI;
  //     // let l: number = (j + k) / 2;
  //     //
  //     // while (err > 1e-9 && iter < 100) {
  //     //   let trial: number = -DM + l + sigma0 / Math.sqrt(a) * (1 - Math.cos(l)) - (1 - R / a) * Math.sin(l);
  //     //   if (trial > 0) k = l; else j = l;
  //     //   l = (j + k) / 2;
  //     //   err = Math.abs(trial);
  //     //   iter++;
  //     //   DE = l;
  //     // }
  //     //
  //     // const r: number = a + (R - a) * Math.cos(DE) + sigma0 * Math.sqrt(a) * Math.sin(DE);
  //     //
  //     // F = 1 - a / R * (1 - Math.cos(DE));
  //     // G = a * sigma0 / Math.sqrt(mu) * (1 - Math.cos(DE)) + R * Math.sqrt(a / mu) * Math.sin(DE);
  //     // Ft = -Math.sqrt(mu * a) / (r * R) * Math.sin(DE);
  //     // Gt = 1 - a / r * (1 - Math.cos(DE));
  //
  //   } else {
  //     throwError(new Error("Not implemented"));
  //   }
  //
  //   const newPosition: Vector3 = new Vector3(
  //     F * position.x + G * velocity.x,
  //     F * position.y + G * velocity.y,
  //     F * position.z + G * velocity.z
  //   );
  //   const newVelocity: Vector3 = new Vector3(
  //     Ft * position.x + Gt * velocity.x,
  //     Ft * position.y + Gt * velocity.y,
  //     Ft * position.z + Gt * velocity.z
  //   );
  //
  //   return [newPosition, newVelocity]
  // }
  //
  // transferDeltaV(vp: Vector3, vs: Vector3, mu: number, orbit_radius: number) {
  //   const vsp: Vector3 = new Vector3();
  //   vsp.subVectors(vs, vp);
  //   const vsp_length: number = vsp.length();
  //   const vo = Math.sqrt(vsp_length * vsp_length + 2 * mu / orbit_radius);
  //   return vo - Math.sqrt(mu / orbit_radius);
  // }
}
