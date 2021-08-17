import {TestBed} from '@angular/core/testing';

import {ApiService, Constant, Constants, Link, Root} from './api.service';
import {HttpClient} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

describe('ApiService', () => {
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
      href: "http://example.org/"
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

    it("should return constant A", () => {
      const expectedValue: Constant = {
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

      service.getConstant("A").subscribe(
        constant => expect(constant).toEqual(expectedValue, 'should return constant A'),
        fail
      );

      const rootReq = httpTestingController.expectOne("https://www.trident.senorpez.com/");
      expect(rootReq.request.method).toEqual('GET');
      rootReq.flush(mockRoot);

      const constantsReq = httpTestingController.expectOne("/constants");
      expect(constantsReq.request.method).toEqual('GET');
      constantsReq.flush(mockConstants);

      const constantReq = httpTestingController.expectOne("/constants/A");
      expect(constantReq.request.method).toEqual('GET');
      constantReq.flush(expectedValue);
    });
  });
});
