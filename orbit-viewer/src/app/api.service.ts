import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

import {filter, first, switchMap} from 'rxjs/operators'
import {Observable} from "rxjs";
import {flatMap} from "rxjs/internal/operators";

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

  private getConstants() {
    return this.getRoot()
      .pipe(flatMap(value => this.http.get<Constants>(value._links["trident-api:constants"].href)));
  }

  getConstant(constant_symbol: string) {
    return this.getConstants()
      .pipe(
        flatMap(constants => constants._embedded["trident-api:constant"]),
        filter(constant => constant.symbol === constant_symbol),
        first(),
        flatMap(constant => this.http.get<Constant>(constant._links.self.href))
      );
  }

  private getSystems(): Observable<SolarSystems> {
    return this.getRoot()
      .pipe(flatMap(value => this.http.get<SolarSystems>(value._links["trident-api:systems"].href)));
  }

  private getSystem(system_id: number): Observable<SolarSystem> {
    return this.getSystems()
      .pipe(
        flatMap(systems => systems._embedded["trident-api:system"]),
        filter(system => system.id === system_id),
        first(),
        flatMap(system => this.http.get<SolarSystem>(system._links.self.href))
      );
  }

  private getStars(system_id: number): Observable<Stars> {
    return this.getSystem(system_id)
      .pipe(flatMap(value => this.http.get<Stars>(value._links["trident-api:stars"].href)));
  }

  getStar(system_id: number, star_id: number): Observable<Star> {
    return this.getStars(system_id)
      .pipe(
        flatMap(stars => stars._embedded["trident-api:star"]),
        filter(star => star.id === star_id),
        first(),
        flatMap(star => this.http.get<Star>(star._links.self.href))
      );
  }

  private getPlanets(system_id: number, star_id: number): Observable<Planets> {
    return this.getStar(system_id, star_id)
      .pipe(switchMap(value => this.http.get<Planets>(value._links["trident-api:planets"].href)));
  }

  getAllPlanets(system_id: number, star_id: number): Observable<Planet> {
    return this.getPlanets(system_id, star_id)
      .pipe(flatMap(planets => planets._embedded["trident-api:planet"]),
        flatMap(planet => this.http.get<Planet>(planet._links.self.href))
      );
  }

  getPlanet(system_id: number, star_id: number, planet_id: number): Observable<Planet> {
    return this.getPlanets(system_id, star_id)
      .pipe(
        flatMap(planets => planets._embedded["trident-api:planet"]),
        filter(planet => planet.id === planet_id),
        first(),
        flatMap(planet => this.http.get<Planet>(planet._links.self.href))
      );
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

export interface Constants {
  _embedded: {
    "trident-api:constant": EmbeddedConstant[];
  };
  _links: {
    self: Link;
    index: Link;
    curies: Curie[];
  };
}

export interface Constant {
  name: string;
  symbol: string;
  value: number;
  units: string;
  _links: {
    self: Link;
    "trident-api:constants": Link;
    index: Link;
    curies: Curie[];
  };
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

export interface Planet {
  id: number;
  name: string;
  mass: number;
  radius: number;
  semimajorAxis: number;
  eccentricity: number;
  inclination: number;
  longitudeOfAscendingNode: number;
  argumentOfPeriapsis: number;
  trueAnomalyAtEpoch: number;

  GM: number;
  starGM: number;

  _links: {
    self: Link;
    "trident-api:calendars": Link;
    "trident-api:planets": Link;
    index: Link;
    curies: Curie[];
  };
}

export interface EmbeddedConstant {
  symbol: string;
  _links: {
    self: Link;
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
