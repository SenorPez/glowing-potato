import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StatsComponent} from './stats.component';
import {SimpleChange, SimpleChanges} from "@angular/core";

describe('StatsComponent class test', () => {
  describe('ngOnChanges', () => {
    let component: StatsComponent;
    let mockClearPie;
    let mockDrawPie;

    beforeEach(() => {
      component = new StatsComponent();
      mockClearPie = spyOn(component, "clearPie");
      mockDrawPie = spyOn(component, "drawPie");
    });

    it('should do nothing with changes.minDV.firstChange', () => {
      const changes: SimpleChanges = {
        minDV: new SimpleChange(42, 11, true)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).not.toHaveBeenCalled();
      expect(component.drawPie).not.toHaveBeenCalled();
    });

    it('should call clearPie with changes.minDV.currentvalue null', () => {
      const changes: SimpleChanges = {
        minDV: new SimpleChange(42, null, false)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).toHaveBeenCalledTimes(1);
      expect(component.drawPie).not.toHaveBeenCalled();
    });

    it('should call drawPie with changes.minDV.currentvalue set', () => {
      const currentValue = Math.round(Math.random() * 100);
      const changes: SimpleChanges = {
        minDV: new SimpleChange(42, currentValue, false)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).not.toHaveBeenCalled();
      expect(component.drawPie).toHaveBeenCalledOnceWith(
        component.minDVCtx,
        currentValue
      );
    });

    it('should do nothing with changes.minFT.firstChange', () => {
      const changes: SimpleChanges = {
        minFT: new SimpleChange(42, 11, true)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).not.toHaveBeenCalled();
      expect(component.drawPie).not.toHaveBeenCalled();
    });

    it('should call clearPie with changes.minFT.currentvalue null', () => {
      const changes: SimpleChanges = {
        minFT: new SimpleChange(42, null, false)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).toHaveBeenCalledTimes(1);
      expect(component.drawPie).not.toHaveBeenCalled();
    });

    it('should call drawPie with changes.minFT.currentvalue set', () => {
      const currentValue = Math.round(Math.random() * 100);
      const changes: SimpleChanges = {
        minFT: new SimpleChange(42, currentValue, false)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).not.toHaveBeenCalled();
      expect(component.drawPie).toHaveBeenCalledOnceWith(
        component.minFTCtx,
        currentValue
      );
    });
  });
});

describe('StatsComponent', () => {
  let component: StatsComponent;
  let fixture: ComponentFixture<StatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
