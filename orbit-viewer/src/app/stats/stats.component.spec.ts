import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StatsComponent} from './stats.component';
import {SimpleChange, SimpleChanges} from "@angular/core";
import Spy = jasmine.Spy;

describe('StatsComponent class test', () => {
  describe('ngOnChanges', () => {
    let component: StatsComponent;
    let mockClearPie: Spy<(ctx: CanvasRenderingContext2D) => void>;
    let mockGetPieData: Spy<(transferData: any) => [number, number, string][]>
    let mockDrawPie: Spy<(ctx: CanvasRenderingContext2D, data: [number, number, string][]) => void>

    beforeEach(() => {
      component = new StatsComponent();
      mockClearPie = spyOn(component, "clearPie");
      mockGetPieData = spyOn(component, "getPieData");
      mockDrawPie = spyOn(component, "drawPie");
    });

    it('should do nothing with changes.minDV.firstChange', () => {
      const changes: SimpleChanges = {
        minDV: new SimpleChange(42, 11, true)
      };
      component.ngOnChanges(changes);
      expect(mockClearPie).not.toHaveBeenCalled();
      expect(mockGetPieData).not.toHaveBeenCalled();
      expect(mockDrawPie).not.toHaveBeenCalled();
    });

    it('should call clearPie with changes.minDV.currentvalue null', () => {
      const changes: SimpleChanges = {
        minDV: new SimpleChange(42, null, false)
      };
      component.ngOnChanges(changes);
      expect(mockClearPie).toHaveBeenCalledTimes(1);
      expect(mockGetPieData).not.toHaveBeenCalled();
      expect(mockDrawPie).not.toHaveBeenCalled();
    });

    it('should call drawPie with changes.minDV.currentvalue set', () => {
      const currentValue = Math.round(Math.random() * 100);
      const changes: SimpleChanges = {
        minDV: new SimpleChange(42, currentValue, false)
      };
      component.ngOnChanges(changes);
      expect(mockClearPie).not.toHaveBeenCalled();
      expect(mockGetPieData).toHaveBeenCalledTimes(1);
      expect(mockDrawPie).toHaveBeenCalledTimes(1);
    });

    it('should do nothing with changes.minFT.firstChange', () => {
      const changes: SimpleChanges = {
        minFT: new SimpleChange(42, 11, true)
      };
      component.ngOnChanges(changes);
      expect(mockClearPie).not.toHaveBeenCalled();
      expect(mockGetPieData).not.toHaveBeenCalled();
      expect(mockDrawPie).not.toHaveBeenCalled();
    });

    it('should call clearPie with changes.minFT.currentvalue null', () => {
      const changes: SimpleChanges = {
        minFT: new SimpleChange(42, null, false)
      };
      component.ngOnChanges(changes);
      expect(component.clearPie).toHaveBeenCalledTimes(1);
      expect(mockGetPieData).not.toHaveBeenCalled();
      expect(mockDrawPie).not.toHaveBeenCalled();
    });

    it('should call drawPie with changes.minFT.currentvalue set', () => {
      const currentValue = Math.round(Math.random() * 100);
      const changes: SimpleChanges = {
        minFT: new SimpleChange(42, currentValue, false)
      };
      component.ngOnChanges(changes);
      expect(mockClearPie).not.toHaveBeenCalled();
      expect(mockGetPieData).toHaveBeenCalledTimes(1);
      expect(mockDrawPie).toHaveBeenCalledTimes(1);
    });
  });

  it('should call clearRect in clearPie', () => {
    const component = new StatsComponent();
    const context = {
      clearRect(x: number, y: number, w: number, h: number) {
      }
    };
    const mockClearRect = spyOn(context, "clearRect");
    component.clearPie(<CanvasRenderingContext2D>context);
    expect(mockClearRect).toHaveBeenCalledOnceWith(0, 0, 200, 200);
  });

  it('should call methods in drawPie', () => {
    const component = new StatsComponent();
    const data: [number, number, string][] = [
      [Math.PI, 42, "plonk"],
      [Math.PI * 0.5, 42, "plonk"],
      [0, 42, "plonk"],
      [Math.PI * -0.5, 42, "plonk"]];

    const context = {
      arc(x: number, y: number, radius: number, startAngle: number, endAngle: number, anticlockwise?: boolean) {
      },
      beginPath() {
      },
      closePath() {
      },
      fill() {
      },
      moveTo(x: number, y: number) {
      },
      stroke() {
      }
    };
    const mockArc = spyOn(context, "arc");
    const mockBeginPath = spyOn(context, "beginPath");
    const mockClosePath = spyOn(context, "closePath");
    const mockFill = spyOn(context, "fill");
    const mockMoveTo = spyOn(context, "moveTo");
    const mockStroke = spyOn(context, "stroke");

    component.drawPie(<CanvasRenderingContext2D>context, data);
    expect(mockArc).toHaveBeenCalledTimes(4);
    expect(mockBeginPath).toHaveBeenCalledTimes(4);
    expect(mockClosePath).toHaveBeenCalledTimes(4);
    expect(mockFill).toHaveBeenCalledTimes(4);
    expect(mockMoveTo).toHaveBeenCalledTimes(4);
    expect(mockStroke).toHaveBeenCalledTimes(4);
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
