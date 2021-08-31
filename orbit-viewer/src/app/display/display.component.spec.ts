import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DisplayComponent} from './display.component';
import {MatCheckboxChange, MatCheckboxModule} from "@angular/material/checkbox";
import {TestbedHarnessEnvironment} from "@angular/cdk/testing/testbed";
import {HarnessLoader} from "@angular/cdk/testing";
import {MatCheckboxHarness} from "@angular/material/checkbox/testing";

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
});

describe('DisplayComponent DOM testing', () => {
  let component: DisplayComponent;
  let fixture: ComponentFixture<DisplayComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed
      .configureTestingModule({
        imports: [MatCheckboxModule],
        declarations: [DisplayComponent]})
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DisplayComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined('should be defined');
  });

  it('orbits mat-checkbox should toggle from true to false', async () => {
    const checkbox: MatCheckboxHarness = await loader.getHarness(MatCheckboxHarness.with({label: "Orbits"}));
    expect(checkbox.isChecked()).toBeTruthy('should be true');
    expect(component.orbitsEvent.subscribe((state: boolean) => expect(state).toBeFalsy('should be false')));
    await checkbox.toggle();
  });

  it('transfers mat-checkbox should toggle from true to false', async () => {
    const checkbox: MatCheckboxHarness = await loader.getHarness(MatCheckboxHarness.with({label: "Transfers"}));
    expect(checkbox.isChecked()).toBeTruthy('should be true');
    expect(component.orbitsEvent.subscribe((state: boolean) => expect(state).toBeFalsy('should be false')));
    await checkbox.toggle();
  });

  it('planet locators mat-checkbox should toggle from true to false', async () => {
    const checkbox: MatCheckboxHarness = await loader.getHarness(MatCheckboxHarness.with({label: "Planet Locators"}));
    expect(checkbox.isChecked()).toBeTruthy('should be true');
    expect(component.orbitsEvent.subscribe((state: boolean) => expect(state).toBeFalsy('should be false')));
    await checkbox.toggle();
  });
});

