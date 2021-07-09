import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private api: string;

  constructor() {
    this.api = "https://www.trident.senorpez.com/"
  }

  private apiRoot() {
    return fetch(this.api, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    })
      .then(response => response.json());
  }

  private apiSystems() {
    return this.apiRoot()
      .then(data => data._links["trident-api:systems"].href)
      .then(systemsURI => {
        return fetch(systemsURI, {
          method: 'GET',
          headers: {
            'Accept': 'application/json'
          }
        });
      })
      .then(response => response.json());
  }

  private apiSystem(system_id: number) {
    return this.apiSystems()
      .then(data => {
        const embeddedSystem = data._embedded["trident-api:system"].find((item: any) => item.id === system_id);
        return embeddedSystem._links.self.href;
      })
      .then(systemURI => {
        return fetch(systemURI, {
          method: 'GET',
          headers: {
            'Accept': 'application/json'
          }
        });
      })
      .then(response => response.json());
  }

  private apiStars(system_id: number) {
    return this.apiSystem(system_id)
      .then(data => data._links["trident-api:stars"].href)
      .then(starsURI => {
        return fetch(starsURI, {
          method: 'GET',
          headers: {
            'Accept': 'application/json'
          }
        });
      })
      .then(response => response.json());
  }

  private apiStar(system_id: number, star_id: number) {
    return this.apiStars(system_id)
      .then(data => {
        const embeddedStar = data._embedded["trident-api:star"].find((item: any) => item.id === star_id);
        return embeddedStar._links.self.href;
      })
      .then(starURI => {
        return fetch(starURI, {
          method: 'GET',
          headers: {
            'Accept': 'application/json'
          }
        });
      })
      .then(response => response.json());
  }

  private apiPlanets(system_id: number, star_id: number) {
    return this.apiStar(system_id, star_id)
      .then(data => data._links["trident-api:planets"].href)
      .then(planetsURI => {
        return fetch(planetsURI, {
          method: 'GET',
          headers: {
            'Accept': 'application/json'
          }
        });
      })
      .then(response => response.json());
  }

  getPlanets(system_id: number, star_id: number): Promise<EmbeddedPlanet[]> {
    return this.apiPlanets(system_id, star_id)
      .then(planets => {
        return planets._embedded["trident-api:planet"].map((planet: any) => {
          const returnValue: EmbeddedPlanet = {
            id: planet.id,
            name: planet.name
          }
          return returnValue;
        });
      });
  }
}

export interface EmbeddedPlanet {
  id: number;
  name: string;
}
