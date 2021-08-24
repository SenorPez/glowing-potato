import {Component, EventEmitter, Output} from '@angular/core';
import {MatCheckboxChange} from "@angular/material/checkbox";

@Component({
  selector: 'app-display',
  templateUrl: './display.component.html',
  styleUrls: ['./display.component.css']
})
export class DisplayComponent {
  @Output() orbitsEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() planetLocatorsEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() transfersEvent: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() { }

  changeOrbits($event: MatCheckboxChange): void {
    this.orbitsEvent.emit($event.checked);
  }

  changePlanetLocators($event: MatCheckboxChange): void {
    this.planetLocatorsEvent.emit($event.checked);
  }

  changeTransfers($event: MatCheckboxChange): void {
    this.transfersEvent.emit($event.checked);
  }
}
