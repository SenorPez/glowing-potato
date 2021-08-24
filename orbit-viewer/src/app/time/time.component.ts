import {Component, EventEmitter, Input, Output} from '@angular/core';
import * as THREE from "three";
import {MatSliderChange} from "@angular/material/slider";
import {Planet} from "../api.service";
import {MatSelectChange} from "@angular/material/select";

@Component({
  selector: 'app-time',
  templateUrl: './time.component.html',
  styleUrls: ['./time.component.css']
})
export class TimeComponent {
  @Input() scene !: THREE.Scene;

  // TODO: Maintain elapsed time in the time component?
  @Input() elapsedTime: number = 0;

  @Input() working: boolean = false;
  @Input() planets: Planet[] = [];
  @Input() points: {point: string, planet: Planet}[] = [];

  @Output() playEvent = new EventEmitter<boolean>();
  @Output() seekEvent = new EventEmitter<boolean>();
  @Output() sliderChangeEvent = new EventEmitter<MatSliderChange>();
  @Output() lambertEvent = new EventEmitter<boolean>();
  @Output() planetsChange = new EventEmitter<(number | null)[]>();

  animating: boolean = false;

  originPlanet: number | null = null;
  targetPlanet: number | null = null;
  invalidTransfer: boolean = true;

  constructor() {
  }

  clickBack(): void {
    this.seekEvent.emit(false);
  }

  clickForward(): void {
    this.seekEvent.emit(true);
  }

  // TODO: Maintain animation state in only one component.
  clickPlay(): void {
    this.animating = !this.animating;
    this.playEvent.emit(this.animating);
  }

  // TODO: Maintain animation speed in only one component.
  newSliderChange($event: MatSliderChange): void {
    this.sliderChangeEvent.emit($event);
  }

  getDate(): string {
    const epochDate = new Date(2000, 0, 1);
    epochDate.setDate(epochDate.getDate() + Math.floor(this.elapsedTime / 86400));

    const months: string[] = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    const returnDate = [
      epochDate.getDate().toString().padStart(2, "0"),
      months[epochDate.getMonth()],
      epochDate.getFullYear().toString(),
      "ST(" + String(Math.floor(this.elapsedTime / 86400 / 14) + 1) + ")"
    ];
    return returnDate.join(" ");
  }

  clickDvLambert(): void {
    this.lambertEvent.emit(true);
  }

  clickFtLambert(): void {
    this.lambertEvent.emit(false);
  }

  changeOrigin($event: MatSelectChange): void {
    this.originPlanet = $event.value;
    this.invalidTransfer = !(
      this.originPlanet !== null
      && this.targetPlanet !== null
      && this.originPlanet !== this.targetPlanet);
    this.planetsChange.emit([this.originPlanet, this.targetPlanet])
  }

  changeTarget($event: MatSelectChange): void {
    this.targetPlanet = $event.value;
    this.invalidTransfer = !(
      this.originPlanet !== null
      && this.targetPlanet !== null
      && this.originPlanet !== this.targetPlanet);
    this.planetsChange.emit([this.originPlanet, this.targetPlanet])
  }
}
