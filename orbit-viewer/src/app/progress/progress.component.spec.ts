import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgressComponent } from './progress.component';
import {HarnessLoader} from "@angular/cdk/testing";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {TestbedHarnessEnvironment} from "@angular/cdk/testing/testbed";
import {MatProgressBarHarness} from "@angular/material/progress-bar/testing";

describe('ProgressComponent DOM testing', () => {
  let component: ProgressComponent;
  let fixture: ComponentFixture<ProgressComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed
      .configureTestingModule({
        imports: [MatProgressBarModule],
        declarations: [ProgressComponent]
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgressComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined('should be created');
  });

  it('mat-progress-bar should be indeterminate', async () => {
    const divLoader = await loader.getChildLoader('#progress');
    const progressBar = await divLoader.getHarness(MatProgressBarHarness);
    expect(await progressBar.getMode()).toBe("indeterminate");
  });
});
