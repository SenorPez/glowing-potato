import {Injectable} from '@angular/core';
import {Vector3} from 'three';
import {Planet} from "./api.service";

@Injectable({
  providedIn: 'root'
})
export class OrbitdataService {
  // TODO: Add AU to API
  private AU: number = 149597870700;

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

  orbitalPeriod(planet: Planet) {
    return 2 * Math.PI * Math.sqrt(Math.pow(planet.semimajorAxis * this.AU, 3) / (planet.starGM + planet.GM));
  }

  propagate(position: Vector3, velocity: Vector3, mu: number, time: number): [Vector3, Vector3] {
    const R: number = position.length();
    const V: number = velocity.length();
    const energy: number = V * V / 2 - mu / R;
    const a: number = -mu / 2.0 / energy;
    const h: Vector3 = new Vector3();
    h.crossVectors(position, velocity);
    const p: number = Math.pow(h.length(), 2) / mu;
    const e: number = Math.sqrt(1 - p / a);

    const sigma0: number = position.dot(velocity) / Math.sqrt(mu);

    let F: number = 1;
    let G: number = 1;
    let Ft: number = 1;
    let Gt: number = 1;

    if (a > 0) {
      const DM: number = Math.sqrt(mu / Math.pow(a, 3)) * time;
      const DE: number = OrbitdataService.meanToEccentric(DM, e);
      const r: number = a + (R - a) * Math.cos(DE) + sigma0 * Math.sqrt(a) * Math.sin(DE);

      F = 1 - a / R * (1 - Math.cos(DE));
      G = a * sigma0 / Math.sqrt(mu) * (1 - Math.cos(DE)) + R * Math.sqrt(a / mu) * Math.sin(DE);
      Ft = -Math.sqrt(mu * a) / (r * R) * Math.sin(DE);
      Gt = 1 - a / r * (1 - Math.cos(DE));
    } else {
      const DN: number = Math.sqrt(-mu / Math.pow(a, 3)) * time;
      const DH: number = OrbitdataService.meanToEccentric(DN, e);
      const r: number = a + (R - a) * Math.cosh(DH) + sigma0 + Math.sqrt(-a) * Math.sinh(DH);

      F = 1 - a / R * (1 - Math.cosh(DH));
      G = a * sigma0 / Math.sqrt(mu) * (1 - Math.cosh(DH)) + R * Math.sqrt(-a / mu) * Math.sinh(DH);
      Ft = -Math.sqrt(-mu * a) / (r * R) * Math.sinh(DH)
      Gt = 1 - a / r * (1 - Math.cosh(DH));
    }

    const newPosition: Vector3 = new Vector3(
      F * position.x + G * velocity.x,
      F * position.y + G * velocity.y,
      F * position.z + G * velocity.z
    );
    const newVelocity: Vector3 = new Vector3(
      Ft * position.x + Gt * velocity.x,
      Ft * position.y + Gt * velocity.y,
      Ft * position.z + Gt * velocity.z
    );

    return [newPosition, newVelocity]
  }


  transfer(r1: Vector3, r2: Vector3, tof: number, mu: number) {
    const m_r1 = r1.length();
    const m_r2 = r2.length();

    const h = new Vector3();
    h.crossVectors(r1, r2);

    // Useful for debugging.
    // const chord = Math.sqrt((r2.x - r1.x) ** 2 + (r2.y - r1.y) ** 2 + (r2.z - r1.z) ** 2);
    // const semiperimeter = (chord + m_r1 + m_r2) / 2.0;
    // const lambda = ((h.z < 1) ? -1 : 1) * Math.sqrt(1 - chord / semiperimeter);
    // const ndToF = tof * Math.sqrt(2 * mu / Math.pow(semiperimeter, 3));

    const cosDeltaNu = r1.dot(r2) / (m_r1 * m_r2);
    let deltaNu = Math.acos(cosDeltaNu);
    if (h.z < 1) deltaNu = 2 * Math.PI - deltaNu;
    let DM = Math.PI - deltaNu < 0 ? -1 : 1;

    const A = DM * Math.sqrt(m_r1 * m_r2 * (1 + cosDeltaNu));

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
      if (z === 0) {
        return 1 / factorial(2);
      } else if (z > 0) {
        return (1 - Math.cos(Math.sqrt(z))) / z;
      } else {
        return (1 - Math.cosh(Math.sqrt(-z))) / z;
      }
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
      if (z === 0) {
        return 1 / factorial(3);
      } else if (z > 0) {
        return (Math.sqrt(z) - Math.sin(Math.sqrt(z))) / Math.sqrt(Math.pow(z, 3));
      } else {
        return (Math.sinh(Math.sqrt(-z)) - Math.sqrt(-z)) / Math.sqrt(Math.pow(-z, 3));
      }
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

    const getY = (z: number, C: number, S: number) => {
      return m_r1 + m_r2 - A * ((1 - z * S) / Math.sqrt(C));
    }

    const calc = (z: number) => {
      const C = expansionC(z);
      const S = expansionS(z);
      const dC = expansionDC(z);
      const dS = expansionDS(z);

      const y = getY(z, C, S);
      const x = Math.sqrt(y / C);

      const t = (Math.pow(x, 3) * S + A * Math.sqrt(y)) / Math.sqrt(mu);

      const dtdz = (x ** 3 * (dS - ((3 * S * dC) / (2 * C))) + (A / 8) * (((3 * S * Math.sqrt(y)) / C) + (A / x))) / Math.sqrt(mu);

      return [t, dtdz, y];
    };

    const findSolution: (maxIterations: number) => (number) = (maxIterations: number) => {
      let z = 0
      let [t, dtdz, y] = calc(z);
      let prevZ = 0;

      let iter = 0;
      const step = 100 / maxIterations;

      while (Math.abs(t - tof) > 10e-4 && iter < maxIterations) {
        iter++;
        prevZ = z;
        z = z + (tof - t) * step / dtdz;

        if (!isFinite(z)) {
          iter = maxIterations;
        }

        let i = 0.50;
        while (getY(z, expansionC(z), expansionS(z)) < 0 && i < 1) {
          z = prevZ === 0 ? 0.01 : prevZ * i;
          i += 0.01;
        }

        [t, dtdz, y] = calc(z);
      }

      if (maxIterations >= 1600) {
        return y;
      } else return iter === maxIterations ? findSolution(maxIterations * 4) : y;
    }

    const y = findSolution(100);

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

  transferDeltaV(vp: Vector3, vs: Vector3, mu: number, orbit_radius: number) {
    const vsp: Vector3 = new Vector3();
    vsp.subVectors(vs, vp);
    const vsp_length: number = vsp.length();
    const vo = Math.sqrt(vsp_length * vsp_length + 2 * mu / orbit_radius);
    return vo - Math.sqrt(mu / orbit_radius);
  }

  private static eccentricToMean(eccentricAnomaly: number, eccentricity: number): number {
    return eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly);
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

  private static trueToEccentric(trueAnomaly: number, eccentricity: number): number {
    return Math.atan2(
      Math.sqrt(1 - Math.pow(eccentricity, 2)) * Math.sin(trueAnomaly),
      eccentricity + Math.cos(trueAnomaly)
    ) % (2 * Math.PI);
  }


  private static trueToMean(trueAnomaly: number, eccentricity: number): number {
    return OrbitdataService.eccentricToMean(OrbitdataService.trueToEccentric(trueAnomaly, eccentricity), eccentricity);
  }



}
