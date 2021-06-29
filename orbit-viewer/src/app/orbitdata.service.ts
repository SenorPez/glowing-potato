import {Injectable} from '@angular/core';
import {Vector3} from 'three';

@Injectable({
  providedIn: 'root'
})
export class OrbitdataService {

  constructor() { }

  getPosition(system_id: number, star_id: number, planet_id: number, t0: number): Promise<Vector3> {
    return fetch('http://127.0.0.1:5000/orbit/position', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        'system_id': system_id,
        'star_id': star_id,
        'planet_id': planet_id,
        't0': t0
      })
    })
      .then(response => response.json())
      .then(data => new Vector3(data.x, data.y, data.z))
  }

  getPath(system_id: number, star_id: number, planet_id: number, t0: number): Promise<Vector3[]> {
    return fetch('http://127.0.0.1:5000/orbit/path', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        'system_id': system_id,
        'star_id': star_id,
        'planet_id': planet_id,
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
}
