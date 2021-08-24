import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TimeComponent} from './time.component';
import {MatSliderChange} from "@angular/material/slider";
import {MatSelectChange} from "@angular/material/select";

describe('TimeComponent class', () => {
  it('should emit false seek event when clicking back button', () => {
    const component: TimeComponent = new TimeComponent();
    component.seekEvent.subscribe((state: boolean) => expect(state).toBeFalse());
    component.clickBack();
  });

  it('should emit true seek event when clicking forward button', () => {
    const component: TimeComponent = new TimeComponent();
    component.seekEvent.subscribe((state: boolean) => expect(state).toBeTrue());
    component.clickForward();
  });

  it('should toggle animating state when clicking play button', () => {
    const component: TimeComponent = new TimeComponent();
    const originalState = Math.random() < 0.5;
    component.animating = originalState;

    component.playEvent.subscribe((state: boolean) => expect(state).toEqual(!originalState));
    component.clickPlay();
  });

  it('should return slider state when changing', () => {
    const component: TimeComponent = new TimeComponent();
    const event: MatSliderChange = new MatSliderChange();
    event.value = Math.random() * 35;
    component.sliderChangeEvent.subscribe((emitEvent: MatSliderChange) => expect(emitEvent).toEqual(event));
    component.newSliderChange(event);
  });

  describe('getDate should format dates', () => {
    const parameters = [
      {description: "Epoch Date", elapsedTime: 0, expectedValue: "01 Jan 2000 ST(1)"},
      {description: "March 29", elapsedTime: 7603200, expectedValue: "29 Mar 2000 ST(7)"},
      {description: "100 Days", elapsedTime: 8640000, expectedValue: "10 Apr 2000 ST(8)"},
      {description: "Leap Day 2004", elapsedTime: 131328000, expectedValue: "29 Feb 2004 ST(109)"}
    ];
    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const component: TimeComponent = new TimeComponent();
        component.elapsedTime = parameter.elapsedTime;
        expect(component.getDate()).toEqual(parameter.expectedValue);
      });
    });
  });

  it('should emit true lambert event when clicking DV button', () => {
    const component: TimeComponent = new TimeComponent();
    component.lambertEvent.subscribe((state: boolean) => expect(state).toBeTrue());
    component.clickDvLambert();
  });

  it('should emit false lambert event when clicking FT button', () => {
    const component: TimeComponent = new TimeComponent();
    component.lambertEvent.subscribe((state: boolean) => expect(state).toBeFalse());
    component.clickFtLambert();
  });

  describe('changeOrigin', () => {
    const parameters = [
      {
        description: 'should be a valid transfer between 11 / 42',
        originPlanet: 11,
        targetPlanet: 42,
        expectedInvalid: false,
        expectedValue: [11, 42]
      },
      {
        description: 'should be an invalid transfer between null / 42',
        originPlanet: null,
        targetPlanet: 42,
        expectedInvalid: true,
        expectedValue: [null, 42]
      },
      {
        description: 'should be an invalid transfer between 11 / null',
        originPlanet: 11,
        targetPlanet: null,
        expectedInvalid: true,
        expectedValue: [11, null]
      },
      {
        description: 'should be an invalid transfer between 42 / 42',
        originPlanet: 42,
        targetPlanet: 42,
        expectedInvalid: true,
        expectedValue: [42, 42]
      }
    ];
    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const component: TimeComponent = new TimeComponent();
        component.targetPlanet = parameter.targetPlanet;
        const event = {value: parameter.originPlanet};
        component.planetsChange.subscribe((data: (number | null)[]) => {
          expect(component.invalidTransfer).toBe(parameter.expectedInvalid);
          expect(data).toEqual(parameter.expectedValue);
        });
        component.changeOrigin(<MatSelectChange>event);
      });
    });
  });

  describe('changeTarget', () => {
    const parameters = [
      {
        description: 'should be a valid transfer between 11 / 42',
        originPlanet: 11,
        targetPlanet: 42,
        expectedInvalid: false,
        expectedValue: [11, 42]
      },
      {
        description: 'should be an invalid transfer between null / 42',
        originPlanet: null,
        targetPlanet: 42,
        expectedInvalid: true,
        expectedValue: [null, 42]
      },
      {
        description: 'should be an invalid transfer between 11 / null',
        originPlanet: 11,
        targetPlanet: null,
        expectedInvalid: true,
        expectedValue: [11, null]
      },
      {
        description: 'should be an invalid transfer between 42 / 42',
        originPlanet: 42,
        targetPlanet: 42,
        expectedInvalid: true,
        expectedValue: [42, 42]
      }
    ];
    parameters.forEach(parameter => {
      it(parameter.description, () => {
        const component: TimeComponent = new TimeComponent();
        component.originPlanet = parameter.originPlanet;
        const event = {value: parameter.targetPlanet};
        component.planetsChange.subscribe((data: (number | null)[]) => {
          expect(component.invalidTransfer).toBe(parameter.expectedInvalid);
          expect(data).toEqual(parameter.expectedValue);
        });
        component.changeTarget(<MatSelectChange>event);
      });
    });
  });
});

describe('TimeComponent', () => {
  let component: TimeComponent;
  let fixture: ComponentFixture<TimeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
