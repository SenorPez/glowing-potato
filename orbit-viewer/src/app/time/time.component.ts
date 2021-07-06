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
  @Output() playEvent = new EventEmitter<boolean>();
  @Output() sliderChangeEvent = new EventEmitter<MatSliderChange>();

  animating: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  clickPlay() {
    this.animating = !this.animating;
    this.playEvent.emit();
  }

  newSliderChange($event: MatSliderChange) {
    this.sliderChangeEvent.emit($event);
  }
}
