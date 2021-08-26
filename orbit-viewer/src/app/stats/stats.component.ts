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

  minDVCtx !: CanvasRenderingContext2D;
  minFTCtx !: CanvasRenderingContext2D;
  private tooltip !: HTMLCanvasElement;

  private dvAngles: [number, number, string][] = [];
  private ftAngles: [number, number, string][] = [];

  colors: [string, string, string, string, string, string] =
    ["#0000FF", "#B3B3FF", "#E0E0FF",
    "#FFE0FF", "#FFB3FF", "#FF00FF"];

  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.minDV !== undefined) {
      if (changes.minDV.firstChange) {
        // Do nothing, init not completed.
      } else if (changes.minDV.currentValue === null) {
        this.clearPie(this.minDVCtx);
      } else {
        this.dvAngles = this.getPieData(changes.minDV.currentValue);
        this.drawPie(this.minDVCtx, this.dvAngles);
      }
    }

    if (changes.minFT !== undefined) {
      if (changes.minFT.firstChange) {
        // Do nothing, init not completed.
      } else if (changes.minFT.currentValue === null) {
        this.clearPie(this.minFTCtx);
      } else {
        this.ftAngles = this.getPieData(changes.minFT.currentValue);
        this.drawPie(this.minFTCtx, this.ftAngles);
      }
    }
  }

  ngOnInit(): void {
    const dvcanvas: HTMLCanvasElement = <HTMLCanvasElement> document.getElementById("minDV");
    dvcanvas.addEventListener('mousemove', e => this.onMouseMove(e));
    dvcanvas.addEventListener('mouseleave', () => this.onMouseLeave());
    const dvctx: CanvasRenderingContext2D | null = dvcanvas.getContext("2d");
    if (dvctx === null) throw new Error("Count not get context");
    this.minDVCtx = dvctx;

    const ftcanvas: HTMLCanvasElement = <HTMLCanvasElement>document.getElementById("minFT");
    ftcanvas.addEventListener('mousemove', e => this.onMouseMove(e));
    ftcanvas.addEventListener('mouseleave', () => this.onMouseLeave());
    const ftctx: CanvasRenderingContext2D | null = ftcanvas.getContext("2d");
    if (ftctx === null) throw new Error("Count not get context");
    this.minFTCtx = ftctx;

    const ttcanvas: HTMLCanvasElement = <HTMLCanvasElement>document.getElementById("tooltip");
    if (ttcanvas === null) throw new Error("Could not get tooltip canvas.");
    this.tooltip = ttcanvas;
    this.tooltip.style.display = 'none';
  }

  clearPie(ctx: CanvasRenderingContext2D) {
    ctx.clearRect(0, 0, 200, 200);
  }

  drawPie(ctx: CanvasRenderingContext2D, data: [number, number, string][]): void {
    let startAngle = Math.PI * 1.5;

    data.forEach((value, index) => {
      const [angle,] = value;
      ctx.beginPath();
      ctx.moveTo(100, 100);
      ctx.arc(100, 100, 75, startAngle, angle, true);
      ctx.closePath();
      ctx.fillStyle = this.colors[index];
      ctx.fill();
      ctx.strokeStyle = "#B0B0B0";
      ctx.stroke();
      startAngle = angle;
    });
  }

  // TODO: Convert to object for easier understanding.
  getPieData(transferData: any): [number, number, string][] {
    const data: [number, number, string][] = [];
    const [usedDV, usedFTsec] = transferData;
    const totalDV = this.maxDV / (1 - this.safetyFactor);

    const usedDVAngle = Math.PI * 1.5 - usedDV / totalDV * Math.PI;
    data.push([usedDVAngle, usedDV, "m/s"]);

    const remainingDV = this.maxDV - usedDV;
    const remainingDVAngle = usedDVAngle - remainingDV / totalDV * Math.PI;
    data.push([remainingDVAngle, remainingDV, "m/s"]);

    data.push([Math.PI * 0.5, totalDV - usedDV - remainingDV, "m/s"]);

    const usedFT = usedFTsec / 86400;
    const remainingFT = this.maxFT - usedFT;
    const totalFT = this.maxFT / (1 - this.safetyFactor);

    const usedFTAngle = usedFT / totalFT * Math.PI;
    const remainingFTAngle = remainingFT / totalFT * Math.PI;

    data.push([Math.PI * -0.5 + usedFTAngle + remainingFTAngle, totalFT - usedFT - remainingFT, "days"]);
    data.push([Math.PI * -0.5 + usedFTAngle, remainingFT, "days"]);
    data.push([Math.PI * -0.5, usedFT, "days"]);

    return data;
  }

  private onMouseMove(event: MouseEvent) {
    const data = this.getData(event);

    if (data !== undefined) {
      const [radius, value, units] = data;
      if (radius <= 75) {
        let offsetX: number;
        if (event.clientX - this.tooltip.width / 2 > 0) {
          offsetX = event.clientX + this.tooltip.width / 2 >= window.innerWidth
            ? event.clientX - this.tooltip.width : this.tooltip.width / 2;
        } else {
          offsetX = event.clientX;
        }

        const id = (<HTMLElement>event.target).id;
        const offsetY = id === "minFT" ? 200 : 0;

        this.tooltip.style.left = (event.offsetX - offsetX).toString() + "px";
        this.tooltip.style.top = (event.offsetY - 25 + offsetY).toString() + "px";

        const tipCtx = this.tooltip.getContext("2d");
        if (tipCtx === null) throw new Error("Cannot get context");
        tipCtx.clearRect(0, 0, 200, 200);
        this.tooltip.style.display = 'block';

        tipCtx.font = 'bold 10px sans-serif';
        const tipWidth = tipCtx.measureText(Math.round(value).toString() + " " + units).width;
        this.tooltip.width = tipWidth + 10;

        tipCtx.textAlign = 'left';
        tipCtx.textBaseline = 'hanging';
        tipCtx.fillStyle = 'white';
        tipCtx.fillText(Math.round(value).toString() + " " + units, 5, 5);
      } else {
        this.tooltip.style.display = 'none';
      }
    }
  }

  private onMouseLeave() {
    this.tooltip.style.display = 'none';
  }

  private getData(event: MouseEvent): [number, number, string] | undefined {
    const bounding = (<HTMLElement>event.target).getBoundingClientRect();
    const id = (<HTMLElement>event.target).id;
    const x = event.clientX - bounding.left;
    const y = event.clientY - bounding.top;
    const radius = Math.sqrt((x - 100) * (x - 100) + (y - 100) * (y - 100));
    let angle = Math.atan2((y - 100), (x - 100)) + (y < 100 ? 2 * Math.PI : 0);
    if (angle > Math.PI * 1.5) angle -= Math.PI * 2;

    const data = id === "minDV" ? this.dvAngles : this.ftAngles;
    const result = data.find(value => {
      const [dataAngle,] = value;
      return dataAngle < angle;
    });
    if (result !== undefined) {
      const [, value, units] = result;
      return [radius, value, units];
    } else {
      return undefined;
    }
  }
}
