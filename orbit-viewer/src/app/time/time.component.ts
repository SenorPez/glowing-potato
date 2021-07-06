import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as THREE from "three";
import {MatSliderChange} from "@angular/material/slider";

@Component({
  selector: 'app-time',
  templateUrl: './time.component.html',
  styleUrls: ['./time.component.css']
})
export class TimeComponent implements OnInit {
  @Input() scene !: THREE.Scene;
  @Input() elapsedTime: number = 0;

  @Output() playEvent = new EventEmitter<boolean>();
  @Output() seekEvent = new EventEmitter<boolean>();
  @Output() sliderChangeEvent = new EventEmitter<MatSliderChange>();

  animating: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  clickBack() {
    this.seekEvent.emit(false);
  }

  clickForward() {
    this.seekEvent.emit(true);
  }

  clickPlay() {
    this.animating = !this.animating;
    this.playEvent.emit();
  }

  newSliderChange($event: MatSliderChange) {
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
}
