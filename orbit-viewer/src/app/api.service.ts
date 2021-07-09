import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

import {switchMap} from 'rxjs/operators'
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = "https://www.trident.senorpez.com/"
  }

  private getRoot(): Observable<Root> {
    return this.http.get<Root>(this.api);
  }

  private getSystems(): Observable<SolarSystems> {
    return this.getRoot()
      .pipe(switchMap(value => this.http.get<SolarSystems>(value._links["trident-api:systems"].href)));
  }

  private getSystem(system_id: number): Observable<SolarSystem> {
    return this.getSystems()
      .pipe(switchMap(value => {
        let system = value._embedded["trident-api:system"].find(system => system.id === system_id);
        return this.http.get<SolarSystem>(<string>system?._links.self.href);
      }));
  }

  private getStars(system_id: number): Observable<Stars> {
    return this.getSystem(system_id)
      .pipe(switchMap(value => this.http.get<Stars>(value._links["trident-api:stars"].href)));
  }

  private getStar(system_id: number, star_id: number) {
    return this.getStars(system_id)
      .pipe(switchMap(value => {
        let star = value._embedded["trident-api:star"].find(star => star.id === star_id);
        return this.http.get<Star>(<string>star?._links.self.href);
      }));
  }

  getPlanets(system_id: number, star_id: number) {
    return this.getStar(system_id, star_id)
      .pipe(switchMap(value => this.http.get<Planets>(value._links["trident-api:planets"].href)));
  }
}

export interface Root {
  _links: {
    self: Link;
    index: Link;
    "trident-api:constants": Link;
    "trident-api:systems": Link;
    curies: Curie[];
  }
}

export interface SolarSystems {
  _embedded: {
    "trident-api:system": EmbeddedSolarSystem[];
  };
  _links: {
    self: Link;
    index: Link;
    curies: Curie[];
  }
}

export interface SolarSystem {
  id: number;
  name: string;
  _links: {
    self: Link;
    "trident-api:stars": Link;
    "trident-api:systems": Link;
    index: Link;
    curies: Curie[];
  }
}

export interface Stars {
  _embedded: {
    "trident-api:star": EmbeddedStar[];
  };
  _links: {
    self: Link;
    "trident-api:system": Link;
    index: Link;
    curies: Curie[];
  }
}

export interface Star {
  id: number,
  name: string,
  mass: number,
  semimajorAxis: number | null,
  eccentricity: number | null,
  inclination: number | null,
  longitudeOfAscendingNode: number | null,
  argumentOfPeriapsis: number | null,
  trueAnomalyAtEpoch: number | null,
  _links: {
    self: Link;
    "trident-api:planets": Link;
    "trident-api:stars": Link;
    index: Link;
    curies: Curie[];
  }
}

export interface Planets {
  _embedded: {
    "trident-api:planet": EmbeddedPlanet[];
  };
  _links: {
    self: Link;
    "trident-api:star": Link;
    index: Link;
    curies: Curie[];
  }
}

export interface EmbeddedSolarSystem {
  id: number;
  name: string;
  _links: {
    self: Link;
  }
}

export interface EmbeddedStar {
  id: number;
  name: string;
  _links: {
    self: Link;
  }
}

export interface EmbeddedPlanet {
  id: number;
  name: string;
  _links: {
    self: Link;
  }
}

export interface Link {
  href: string;
}

export interface Curie {
  href: string;
  name: string;
  templated: boolean;
}
