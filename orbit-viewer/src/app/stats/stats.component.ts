import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.css']
})
export class StatsComponent implements OnInit, OnChanges {
  @Input() minDV: [number, number] | null = null;
  @Input() minFT: [number, number] | null = null;

  // TODO: Customizable ship performance.
  private maxDV = 71250; // 75% of 95 km / sec
  private maxFT = 147; // 75% of 28 week endurance
  private safetyFactor = 0.25;

  private minDVCtx !: CanvasRenderingContext2D;
  private minFTCtx !: CanvasRenderingContext2D;

  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.minDV !== undefined && !changes.minDV.isFirstChange()) {
      const [usedDV, usedFTsec] = changes.minDV.currentValue;
      const remainingDV = this.maxDV - usedDV;
      const totalDV = this.maxDV / (1 - this.safetyFactor);

      const usedDVAngle = usedDV / totalDV * Math.PI;
      const remainingDVAngle = remainingDV / totalDV * Math.PI;

      const usedFT = usedFTsec / 86400;
      const remainingFT = this.maxFT - usedFT;
      const totalFT = this.maxFT / (1 - this.safetyFactor);

      const usedFTAngle = usedFT / totalFT * Math.PI;
      const remainingFTAngle = remainingFT / totalFT * Math.PI;

      this.drawPie(
        this.minDVCtx, [usedDVAngle, "#0000FF"], [remainingDVAngle, "#B3B3FF"], "#E0E0FF",
        [usedFTAngle, "#FF00FF"], [remainingFTAngle, "#FFB3FF"], "#FFE0FF"
      );
    } else if (changes.minFT !== undefined && !changes.minFT.isFirstChange()) {
      const [usedDV, usedFTsec] = changes.minFT.currentValue;
      const remainingDV = this.maxDV - usedDV;
      const totalDV = this.maxDV / (1 - this.safetyFactor);

      const usedDVAngle = usedDV / totalDV * Math.PI;
      const remainingDVAngle = remainingDV / totalDV * Math.PI;

      const usedFT = usedFTsec / 86400;
      const remainingFT = this.maxFT - usedFT;
      const totalFT = this.maxFT / (1 - this.safetyFactor);

      const usedFTAngle = usedFT / totalFT * Math.PI;
      const remainingFTAngle = remainingFT / totalFT * Math.PI;

      this.drawPie(
        this.minFTCtx, [usedDVAngle, "#0000FF"], [remainingDVAngle, "#B3B3FF"], "#E0E0FF",
        [usedFTAngle, "#FF00FF"], [remainingFTAngle, "#FFB3FF"], "#FFE0FF"
      );
    }
  }

  ngOnInit(): void {
    const dvcanvas: HTMLCanvasElement = <HTMLCanvasElement> document.getElementById("minDV");
    const dvctx: CanvasRenderingContext2D | null = dvcanvas.getContext("2d");
    if (dvctx === null) throw new Error("Count not get context");
    this.minDVCtx = dvctx;

    const ftcanvas: HTMLCanvasElement = <HTMLCanvasElement>document.getElementById("minFT");
    const ftctx: CanvasRenderingContext2D | null = ftcanvas.getContext("2d");
    if (ftctx === null) throw new Error("Count not get context");
    this.minFTCtx = ftctx;
  }

  drawPie(ctx: CanvasRenderingContext2D,
          usedDV: [number, string], remainingDV: [number, string], safetyColorDV: string,
          usedFT: [number, string], remainingFT: [number, string], safetyColorFT: string) {

    const drawHalfPie = (used: [number, string], remaining: [number, string], safetyColor: string, ccw: boolean) => {
      const [usedAngle, usedColor] = used;
      const [remainingAngle, remainingColor] = remaining;
      let startAngle = Math.PI * 1.5;
      let multi = ccw ? -1 : 1;

      ctx.beginPath()
      ctx.moveTo(100, 100);
      ctx.arc(100, 100, 75, startAngle, startAngle + usedAngle * multi, ccw);
      ctx.closePath();
      ctx.fillStyle = usedColor;
      ctx.fill();
      ctx.strokeStyle = "B0B0B0";
      ctx.stroke();

      startAngle += usedAngle * multi;

      ctx.beginPath()
      ctx.moveTo(100, 100);
      ctx.arc(100, 100, 75, startAngle, startAngle + remainingAngle * multi, ccw);
      ctx.closePath();
      ctx.fillStyle = remainingColor;
      ctx.fill();
      ctx.strokeStyle = "B0B0B0";
      ctx.stroke();

      startAngle += remainingAngle * multi;

      ctx.beginPath();
      ctx.moveTo(100, 100);
      ctx.arc(100, 100, 75, startAngle, Math.PI / 2, ccw);
      ctx.closePath();
      ctx.fillStyle = safetyColor;
      ctx.fill();
      ctx.strokeStyle = "#B0B0B0";
      ctx.stroke();
    }

    drawHalfPie(usedDV, remainingDV, safetyColorDV, true);
    drawHalfPie(usedFT, remainingFT, safetyColorFT, false);
  }
}
