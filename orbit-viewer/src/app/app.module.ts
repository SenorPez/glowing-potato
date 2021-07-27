import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {OrbitComponent} from './orbit/orbit.component';
import {TimeComponent} from './time/time.component';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatSliderModule} from "@angular/material/slider";
import {FormsModule} from "@angular/forms";
import {DisplayComponent} from './display/display.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ProgressComponent} from './progress/progress.component';
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {HttpClientModule} from "@angular/common/http";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatOptionModule} from "@angular/material/core";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { StatsComponent } from './stats/stats.component';

@NgModule({
  declarations: [
    AppComponent,
    OrbitComponent,
    TimeComponent,
    DisplayComponent,
    ProgressComponent,
    StatsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatIconModule,
    MatButtonModule,
    MatSliderModule,
    FormsModule,
    MatCheckboxModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
