import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayComponent } from './display.component';
import {MatCheckboxChange} from "@angular/material/checkbox";

describe('DisplayComponent class', () => {
  it('should emit true state for orbit check box', () => {
    const component: DisplayComponent = new DisplayComponent();
    const event: MatCheckboxChange = new MatCheckboxChange();
    event.checked = true;
    component.orbitsEvent.subscribe((state: boolean) => expect(state).toBeTruthy('should be true'));
    component.changeOrbits(event);
  });

  it('should emit false state for orbit check box', () => {
    const component: DisplayComponent = new DisplayComponent();
    const event: MatCheckboxChange = new MatCheckboxChange();
    event.checked = false;
    component.orbitsEvent.subscribe((state: boolean) => expect(state).toBeFalsy('should be false'));
    component.changeOrbits(event);
  });

  it('should emit false state for planet locators check box', () => {
    const component: DisplayComponent = new DisplayComponent();
    const event: MatCheckboxChange = new MatCheckboxChange();
    event.checked = true;
    component.planetLocatorsEvent.subscribe((state: boolean) => expect(state).toBeTruthy('should be true'));
    component.changePlanetLocators(event);
  });

  it('should emit true state for planet locators check box', () => {
    const component: DisplayComponent = new DisplayComponent();
    const event: MatCheckboxChange = new MatCheckboxChange();
    event.checked = false;
    component.planetLocatorsEvent.subscribe((state: boolean) => expect(state).toBeFalsy('should be false'));
    component.changePlanetLocators(event);
  });

  it('should emit true state for transfers check box', () => {
    const component: DisplayComponent = new DisplayComponent();
    const event: MatCheckboxChange = new MatCheckboxChange();
    event.checked = true;
    component.transfersEvent.subscribe((state: boolean) => expect(state).toBeTruthy('should be true'));
    component.changeTransfers(event);
  });

  it('should emit false state for transfers check box', () => {
    const component: DisplayComponent = new DisplayComponent();
    const event: MatCheckboxChange = new MatCheckboxChange();
    event.checked = false;
    component.transfersEvent.subscribe((state: boolean) => expect(state).toBeFalsy('should be false'));
    component.changeTransfers(event);
  });
})

// describe('DisplayComponent', () => {
//   let component: DisplayComponent;
//   let fixture: ComponentFixture<DisplayComponent>;
//
//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       declarations: [ DisplayComponent ]
//     })
//     .compileComponents();
//   });
//
//   beforeEach(() => {
//     fixture = TestBed.createComponent(DisplayComponent);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });
//
//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });
// });
