import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { OrbitComponent } from './orbit/orbit.component';
import { TimeComponent } from './time/time.component';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatSliderModule} from "@angular/material/slider";
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    OrbitComponent,
    TimeComponent
  ],
  imports: [
    BrowserModule,
    MatIconModule,
    MatButtonModule,
    MatSliderModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
