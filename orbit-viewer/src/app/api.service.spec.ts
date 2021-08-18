import {TestBed} from '@angular/core/testing';

import {ApiService, Constant, Constants, Link, Root} from './api.service';
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

    const mockConstantB: Constant = {
      name: "B Constant",
      symbol: "B",
      value: 11,
      units: "baseballs",
      _links: {
        self: dummyLink,
        "trident-api:constants": dummyLink,
        "index": dummyLink,
        curies: []
      }
    };

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
      })

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
});
