import {Injectable} from '@angular/core';
import {Vector3} from 'three';
import {Planet} from "./planet";

@Injectable({
  providedIn: 'root'
})
export class OrbitdataService {

  constructor() { }

  getPath(system_id: number, star_id: number, planet_id: number): Promise<Vector3[]> {
    return fetch('http://127.0.0.1:5000/orbit/path', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        'system_id': system_id,
        'star_id': star_id,
        'planet_id': planet_id
      })
    })
      .then(response => response.json())
      .then(data => {
        return data.x.map(function (element: number, index: number) {
          return new Vector3(element, data.y[index], data.z[index]);
        });
      })
  }

  getPlanet(system_id: number, star_id: number, planet_id:number): Promise<Planet> {
    return fetch("http://127.0.0.1:5000/orbit/planet", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        'system_id': system_id,
        'star_id': star_id,
        'planet_id': planet_id
      })
    })
      .then(response => response.json())
      .then(data => {
        const planet: Planet = {
          name: data['name'],
          mass: data['mass'],
          radius: data['radius'],
          semimajorAxis: data['semimajorAxis'],
          eccentricity: data['eccentricity'],
          inclination: data['inclination'],
          longitudeOfAscendingNode: data['longitudeOfAscendingNode'],
          argumentOfPeriapsis: data['argumentOfPeriapsis'],
          trueAnomalyAtEpoch: data['trueAnomalyAtEpoch'],
          starGM: data['starGM'],
          GM: data['GM']
        };
        return planet;
      }
    )
  }

  getRpln(): Promise<number> {
    return fetch('http://127.0.0.1:5000/orbit/Rpln', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => data.Rpln);
  }

  getEarthPosition(t0: number): Promise<Vector3> {
    return fetch('http://127.0.0.1:5000/orbit/earthposition', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        't0': t0
      })
    })
      .then(response => response.json())
      .then(data => new Vector3(data.x, data.y, data.z))
  }

  getEarthPath(t0: number): Promise<Vector3[]> {
    return fetch('http://127.0.0.1:5000/orbit/earthpath', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        't0': t0
      })
    })
      .then(response => response.json())
      .then(data => {
        return data.x.map(function (element: number, index: number) {
          return new Vector3(element, data.y[index], data.z[index]);
        });
      })
  }

  private static getMeanMotion(planet: Planet): number {
    const AU: number = 149598000000;
    return Math.sqrt((planet.starGM + planet.GM) / Math.pow(planet.semimajorAxis * AU, 3));
  }

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

  ephemeris(planet: Planet, time: number): [Vector3, Vector3] {
    const dt: number = time;

    const meanMotion = OrbitdataService.getMeanMotion(planet);
    const meanAnomaly = OrbitdataService.trueToMean(planet.trueAnomalyAtEpoch, planet.eccentricity) + meanMotion * dt;
    const eccentricAnomaly = OrbitdataService.meanToEccentric(meanAnomaly, planet.eccentricity);

    const semiminorAxis = planet.semimajorAxis * Math.sqrt(1 - planet.eccentricity * planet.eccentricity);

    const xPer = planet.semimajorAxis * (Math.cos(eccentricAnomaly) - planet.eccentricity);
    const yPer = semiminorAxis * Math.sin(eccentricAnomaly);
    const xDotPer = -(planet.semimajorAxis * meanMotion * Math.sin(eccentricAnomaly)) / (1 - planet.eccentricity * Math.cos(eccentricAnomaly));
    const yDotPer = (semiminorAxis * meanMotion * Math.cos(eccentricAnomaly)) / (1 - planet.eccentricity * Math.cos(eccentricAnomaly));

    const cosLoAN = Math.cos(planet.longitudeOfAscendingNode);
    const cosAoP = Math.cos(planet.argumentOfPeriapsis);
    const cosInc = Math.cos(planet.inclination);
    const sinLoAN = Math.sin(planet.longitudeOfAscendingNode);
    const sinAoP = Math.sin(planet.argumentOfPeriapsis);
    const sinInc = Math.sin(planet.inclination);

    const rotationMatrix: number[][] = [[], [], []];
    rotationMatrix[0][0] = cosLoAN * cosAoP - sinLoAN * sinAoP * cosInc;
    rotationMatrix[0][1] = -cosLoAN * sinAoP - sinLoAN * cosAoP * cosInc;
    rotationMatrix[0][2] = sinLoAN * sinInc;
    rotationMatrix[1][0] = sinLoAN * cosAoP + cosLoAN * sinAoP * cosInc;
    rotationMatrix[1][1] = -sinLoAN * sinAoP + cosLoAN * cosAoP * cosInc;
    rotationMatrix[1][2] = -cosLoAN * sinInc;
    rotationMatrix[2][0] = sinAoP * sinInc;
    rotationMatrix[2][1] = cosAoP * sinInc;
    rotationMatrix[2][2] = cosInc;

    const peri1: number[] = [xPer, yPer, 0.0];
    const peri2: number[] = [xDotPer, yDotPer, 0.0];
    const r: number[] = [];
    const v: number[] = [];

    for (var j = 0; j < 3; j++) {
      r[j] = 0.0;
      v[j] = 0.0;
      for (var k = 0; k < 3; k++) {
        r[j] += rotationMatrix[j][k] * peri1[k];
        v[j] += rotationMatrix[j][k] * peri2[k];
      }
    }
    const position: Vector3 = new Vector3(r[0], r[1], r[2]);
    const velocity: Vector3 = new Vector3(v[0], v[1], v[2]);
    return [position, velocity];
  }
}
