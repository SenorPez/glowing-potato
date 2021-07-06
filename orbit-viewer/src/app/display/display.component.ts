import {Component, EventEmitter, Output} from '@angular/core';
import {MatCheckboxChange} from "@angular/material/checkbox";

@Component({
  selector: 'app-display',
  templateUrl: './display.component.html',
  styleUrls: ['./display.component.css']
})
export class DisplayComponent {
  @Output() orbitsEvent = new EventEmitter<boolean>();
  @Output() planetLocatorsEvent = new EventEmitter<boolean>();

  constructor() { }

  changeOrbits($event: MatCheckboxChange) {
    this.orbitsEvent.emit($event.checked);
  }

  changePlanetLocators($event: MatCheckboxChange) {
    this.planetLocatorsEvent.emit($event.checked);
  }
}
