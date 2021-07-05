import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as THREE from "three";

@Component({
  selector: 'app-time',
  templateUrl: './time.component.html',
  styleUrls: ['./time.component.css']
})
export class TimeComponent implements OnInit {
  @Input() scene !: THREE.Scene;
  @Output() playEvent = new EventEmitter<boolean>();

  animating: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  clickPlay() {
    this.animating = !this.animating;
    this.playEvent.emit();
  }
}
