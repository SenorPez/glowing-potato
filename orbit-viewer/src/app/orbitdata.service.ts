import {Injectable} from '@angular/core';
import {Quaternion, Vector3} from 'three';
import {Planet} from "./api.service";

@Injectable({
  providedIn: 'root'
})
export class OrbitdataService {
  // TODO: Add AU to API
  private AU: number = 149597870700;

  // TODO: Add lagrange stability to API.
  lagrangeStability(solarMass: number, planetMass: number): boolean {
    return solarMass > (25 + Math.sqrt(621)) / 2 * planetMass;
  }

  lagrangePoint(planet: Planet, time: number, L4: boolean): Vector3 {
    // Current planet position.
    const [position]: [Vector3, Vector3] = this.ephemerides(planet, time);
    const degrees = L4 ? 60 : -60;

    const quaternion = new Quaternion();
    quaternion.setFromAxisAngle(new Vector3(0, 0, 1), degrees * Math.PI / 180);
    position.applyQuaternion(quaternion);

    return position;
  }

  ephemerides(planet: Planet, time: number): [Vector3, Vector3] {
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

  propagate(r0: Vector3, v0: Vector3, mu: number, time: number) {
    const m_r0 = r0.length();
    const m_v0 = v0.length();
    const energy = m_v0 * m_v0 / 2 - mu / m_r0;
    const a = -mu / (2 * energy);
    const h: Vector3 = new Vector3();
    h.crossVectors(r0, v0);
    const p = Math.pow(h.length(), 2) / mu;
    const e = Math.sqrt(1 - p / a);

    const calc = (x: number) => {
      const z = x * x / a;
      const t = ((r0.dot(v0) / Math.sqrt(mu)) * x * x * OrbitdataService.expansionC(z)
        + (1 - m_r0 / a) * Math.pow(x, 3) * OrbitdataService.expansionS(z)
        + m_r0 * x) / Math.sqrt(mu);
      const dtdx = (x * x * OrbitdataService.expansionC(z)
        + (r0.dot(v0) / Math.sqrt(mu)) * x * (1 - z * OrbitdataService.expansionS(z))
        + m_r0 * (1 - z * OrbitdataService.expansionC(z))) / Math.sqrt(mu);

      return [t, dtdx];
    }

    const findSolution: (maxIterations: number) => [number, number] = (maxIterations: number) => {
      let x = Math.sqrt(mu) * time / a;
      let [t, dtdx] = calc(x);
      let prevX = 0;
      let iter = 0;
      const step = 100 / maxIterations;

      while (Math.abs(t - time) > 1e-5 && iter < maxIterations) {
        iter++
        prevX = x;
        x = x + (time - t) * step / dtdx;
        [t, dtdx] = calc(x);
      }

      return [x, t];
    }

    let [x, t] = findSolution(100);
    const z = x * x / a;

    const f = 1 - (x * x / m_r0) * OrbitdataService.expansionC(z);
    const g = t - (x * x * x / Math.sqrt(mu)) * OrbitdataService.expansionS(z);
    const r = new Vector3(
      f * r0.x + g * v0.x,
      f * r0.y + g * v0.y,
      f * r0.z + g * v0.z
    );

    const m_r = r.length();
    const fdot = (Math.sqrt(mu) / (m_r0 * m_r)) * x * (z * OrbitdataService.expansionS(z) - 1);
    const gdot = 1 - (x * x / m_r) * OrbitdataService.expansionC(z);
    const v = new Vector3(
      fdot * r0.x + gdot * v0.x,
      fdot * r0.y + gdot * v0.y,
      fdot * r0.z + gdot * v0.z,
    )

    return [r, v];
  }

  private static expansionC(z: number) {
    if (z === 0) {
      return 1 / this.factorial(2);
    } else if (z > 0) {
      return (1 - Math.cos(Math.sqrt(z))) / z;
    } else {
      return (1 - Math.cosh(Math.sqrt(-z))) / z;
    }
  }

  private static expansionS(z: number) {
    if (z === 0) {
      return 1 / OrbitdataService.factorial(3);
    } else if (z > 0) {
      return (Math.sqrt(z) - Math.sin(Math.sqrt(z))) / Math.sqrt(Math.pow(z, 3));
    } else {
      return (Math.sinh(Math.sqrt(-z)) - Math.sqrt(-z)) / Math.sqrt(Math.pow(-z, 3));
    }
  }

  private static factorial(n: number): number {
    if (n < 0) {
      return -1;
    } else if (n == 0) {
      return 1;
    } else {
      return n * this.factorial(n - 1);
    }
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

    const expansionDC = (z: number) => {
      return z === 0 ? 1 / OrbitdataService.factorial(4) : (1 / (2 * z)) * (1 - z * OrbitdataService.expansionS(z) - 2 * OrbitdataService.expansionC(z));

      // TODO: Figure out what I'm doing wrong.
      // let sum = 1 / factorial(4);
      // for (let i = 1; i < 101; i++) {
      //   const sign = i % 2 ? 1 : -1;
      //   sum += sign * 2 * Math.pow(z, i) / factorial(4 + i * 2);
      // }
      // return sum;
    }

    const expansionDS = (z: number) => {
      return z === 0 ? 1 / OrbitdataService.factorial(5) : (1 / (2 * z)) * (OrbitdataService.expansionC(z) - 3 * OrbitdataService.expansionS(z));

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
      const C = OrbitdataService.expansionC(z);
      const S = OrbitdataService.expansionS(z);
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
        while (getY(z, OrbitdataService.expansionC(z), OrbitdataService.expansionS(z)) < 0 && i < 1) {
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
