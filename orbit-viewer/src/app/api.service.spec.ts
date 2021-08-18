import {TestBed} from '@angular/core/testing';

import {ApiService, Constant, Constants, Link, Root, SolarSystem, SolarSystems, Star, Stars} from './api.service';
import {HttpClient} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import objectContaining = jasmine.objectContaining;

describe('ApiService unit tests', () => {
  let service: ApiService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ApiService
      ]
    });
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ApiService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe("getConstant", () => {
    const dummyLink: Link = {
      href: "/"
    };

    const mockRoot: Root = {
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:constants": {href: "/constants"},
        "trident-api:systems": dummyLink
      }
    };

    const mockConstants: Constants = {
      _embedded: {
        "trident-api:constant": [
          {"symbol": "A", _links: {self: {"href": "/constants/A"}}},
          {"symbol": "B", _links: {self: {"href": "/constants/B"}}}
        ]
      },
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink
      }
    };

    const mockConstantA: Constant = {
      name: "A Constant",
      symbol: "A",
      value: 42,
      units: "widgets",
      _links: {
        self: dummyLink,
        "trident-api:constants": dummyLink,
        "index": dummyLink,
        curies: []
      }
    };

    it("should 404 on getRoot", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: 'Not Found'};

      service.getConstant("A").subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      rootReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/constants");
      httpTestingController.expectNone("/constants/A");
      httpTestingController.expectNone("/constants/B");
      httpTestingController.expectNone("/constants/X");
    });

    it("should 404 on getConstants", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: "Not Found"};

      service.getConstant("A").subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const constantsReq = httpTestingController.expectOne("/constants");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      constantsReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/constants/A");
      httpTestingController.expectNone("/constants/B");
      httpTestingController.expectNone("/constants/X");
    });

    it("should 404 on getConstant", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: "Not Found"};

      service.getConstant("A").subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const constantsReq = httpTestingController.expectOne("/constants");
      expect(constantsReq.request.method).toEqual('GET');
      constantsReq.flush(mockConstants);

      const constantReq = httpTestingController.expectOne("/constants/A");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      constantReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/constants/B");
      httpTestingController.expectNone("/constants/X");
    });

    it("should return constant A", (done: DoneFn) => {
      const expectedValue: Constant = mockConstantA

      service.getConstant("A").subscribe({
        next(constant: Constant): void {
          expect(constant).toEqual(expectedValue, "should return constant A");
          done();
        },
        error(err: any): void {
          fail(err);
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const constantsReq = httpTestingController.expectOne("/constants");
      expect(constantsReq.request.method).toEqual('GET');
      constantsReq.flush(mockConstants);

      const constantReq = httpTestingController.expectOne("/constants/A");
      expect(constantReq.request.method).toEqual('GET');
      constantReq.flush(mockConstantA);

      httpTestingController.expectNone("/constants/B");
      httpTestingController.expectNone("/constants/X");
    });

    it("should throw NotFoundError", (done: DoneFn) => {
      const expectedValue = {name: "NotFoundError", message: "Constant X not found"};

      service.getConstant("X").subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const constantsReq = httpTestingController.expectOne("/constants");
      expect(constantsReq.request.method).toEqual('GET');
      constantsReq.flush(mockConstants);

      httpTestingController.expectNone("/constants/A");
      httpTestingController.expectNone("/constants/B");
      httpTestingController.expectNone("/constants/X");
    });
  });

  describe("getStar", () => {
    const dummyLink: Link = {
      href: "/"
    };

    const mockRoot: Root = {
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:constants": dummyLink,
        "trident-api:systems": {href: "/systems"}
      }
    };

    const mockSystems: SolarSystems = {
      _embedded: {
        "trident-api:system": [
          {id: 11, name: "System 11", _links: {self: {href: "/systems/11"}}},
          {id: 8675309, name: "System 8675309", _links: {self: {href: "/systems/8675309"}}}
        ]
      },
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink
      }
    };

    const mockSystem: SolarSystem = {
      id: 11,
      name: "System 11",
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:systems": dummyLink,
        "trident-api:stars": {href: "/systems/11/stars"}
      }
    };

    const mockStars: Stars = {
      _embedded: {
        "trident-api:star": [
          {id: 42, name: "Star 42", _links: {self: {href: "/systems/11/stars/42"}}},
          {id: 37, name: "Star 37", _links: {self: {href: "/systems/11/stars/37"}}}
        ]
      },
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:system": dummyLink
      }
    };

    const mockStar42: Star = {
      id: 42,
      name: "Star 42",
      mass: 0.75,
      semimajorAxis: null,
      eccentricity: null,
      inclination: null,
      longitudeOfAscendingNode: null,
      argumentOfPeriapsis: null,
      trueAnomalyAtEpoch: null,
      _links: {
        curies: [],
        index: dummyLink,
        self: dummyLink,
        "trident-api:planets": dummyLink,
        "trident-api:stars": dummyLink
      }
    }

    it("should 404 on getRoot", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: 'Not Found'};

      service.getStar(11, 42).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      rootReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/systems");
      httpTestingController.expectNone("/systems/11");
      httpTestingController.expectNone("/systems/11/stars");
      httpTestingController.expectNone("/systems/11/stars/42");
      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should 404 on getSystems", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: 'Not Found'};

      service.getStar(11, 42).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      systemsReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/systems/11");
      httpTestingController.expectNone("/systems/11/stars");
      httpTestingController.expectNone("/systems/11/stars/42");
      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should 404 on getSystem", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: 'Not Found'};

      service.getStar(11, 42).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      expect(systemsReq.request.method).toEqual('GET');
      systemsReq.flush(mockSystems);

      const systemReq = httpTestingController.expectOne("/systems/11");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      systemReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/systems/11/stars");
      httpTestingController.expectNone("/systems/11/stars/42");
      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should 404 on getStars", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: 'Not Found'};

      service.getStar(11, 42).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      expect(systemsReq.request.method).toEqual('GET');
      systemsReq.flush(mockSystems);

      const systemReq = httpTestingController.expectOne("/systems/11");
      expect(systemReq.request.method).toEqual('GET');
      systemReq.flush(mockSystem);

      const starsReq = httpTestingController.expectOne("/systems/11/stars");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      starsReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/systems/11/stars/42");
      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should 404 on getStar", (done: DoneFn) => {
      const expectedValue = {status: 404, statusText: 'Not Found'};

      service.getStar(11, 42).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      expect(systemsReq.request.method).toEqual('GET');
      systemsReq.flush(mockSystems);

      const systemReq = httpTestingController.expectOne("/systems/11");
      expect(systemReq.request.method).toEqual('GET');
      systemReq.flush(mockSystem);

      const starsReq = httpTestingController.expectOne("/systems/11/stars");
      expect(starsReq.request.method).toEqual('GET');
      starsReq.flush(mockStars);

      const starReq = httpTestingController.expectOne("/systems/11/stars/42");
      const errorEvent: ErrorEvent = new ErrorEvent("Not Found");
      starReq.error(errorEvent, {status: 404, statusText: "Not Found"});

      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should return star 42", (done: DoneFn) => {
      const expectedValue: Star = mockStar42;

      service.getStar(11, 42).subscribe({
        next(star: Star): void {
          expect(star).toEqual(expectedValue, "should return star 42");
          done();
        },
        error(err: any): void {
          fail(err);
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      expect(systemsReq.request.method).toEqual('GET');
      systemsReq.flush(mockSystems);

      const systemReq = httpTestingController.expectOne("/systems/11");
      expect(systemReq.request.method).toEqual('GET');
      systemReq.flush(mockSystem);

      const starsReq = httpTestingController.expectOne("/systems/11/stars");
      expect(starsReq.request.method).toEqual('GET');
      starsReq.flush(mockStars);

      const starReq = httpTestingController.expectOne("/systems/11/stars/42");
      expect(starReq.request.method).toEqual('GET');
      starReq.flush(mockStar42);

      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should throw NotFoundError for system", (done: DoneFn) => {
      const expectedValue = {name: "NotFoundError", message: "System 5 not found"};

      service.getStar(5, 42).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      expect(systemsReq.request.method).toEqual('GET');
      systemsReq.flush(mockSystems);

      httpTestingController.expectNone("/systems/11");
      httpTestingController.expectNone("/systems/11/stars");
      httpTestingController.expectNone("/systems/11/stars/42");
      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });

    it("should throw NotFoundError for star", (done: DoneFn) => {
      const expectedValue = {name: "NotFoundError", message: "Star 5 not found in System 11"};

      service.getStar(11, 5).subscribe({
        next(): void {
          fail("should not return anything");
          done();
        },
        error(err: any): void {
          expect(err).toEqual(objectContaining(expectedValue));
          done();
        }
      });

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const systemsReq = httpTestingController.expectOne("/systems");
      expect(systemsReq.request.method).toEqual('GET');
      systemsReq.flush(mockSystems);

      const systemReq = httpTestingController.expectOne("/systems/11");
      expect(systemReq.request.method).toEqual('GET');
      systemReq.flush(mockSystem);

      const starsReq = httpTestingController.expectOne("/systems/11/stars");
      expect(starsReq.request.method).toEqual('GET');
      starsReq.flush(mockStars);

      httpTestingController.expectNone("/systems/11/stars/42");
      httpTestingController.expectNone("/systems/11/stars/37");
      httpTestingController.expectNone("/systems/86753029/stars/42");
      httpTestingController.expectNone("/systems/86753029/stars/37");
    });
  });
});
