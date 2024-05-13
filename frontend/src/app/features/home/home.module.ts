import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {HomeRoutingModule} from './home-routing.module';
import {HomePageComponent} from './components/home-page/home-page.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {KnobModule} from "primeng/knob";
import {FormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {ChartModule} from "primeng/chart";
import {TableModule} from "primeng/table";
import {RatingModule} from "primeng/rating";


@NgModule({
  declarations: [
    HomePageComponent,
    DashboardComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    KnobModule,
    FormsModule,
    DropdownModule,
    ChartModule,
    TableModule,
    RatingModule
  ]
})
export class HomeModule {
}
