import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {HomeRoutingModule} from './home-routing.module';
import {HomePageComponent} from './components/home-page/home-page.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {KnobModule} from "primeng/knob";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {ChartModule} from "primeng/chart";
import {TableModule} from "primeng/table";
import {RatingModule} from "primeng/rating";
import {CalendarModule} from "primeng/calendar";
import {ToastModule} from "primeng/toast";
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {DividerModule} from "primeng/divider";
import {FieldsetModule} from "primeng/fieldset";
import {CardModule} from "primeng/card";
import {PanelModule} from "primeng/panel";
import {CarouselModule} from "primeng/carousel";
import {StyleClassModule} from "primeng/styleclass";
import {RippleModule} from "primeng/ripple";


@NgModule({
    declarations: [
        HomePageComponent,
        DashboardComponent
    ], imports: [
        CommonModule,
        HomeRoutingModule,
        KnobModule,
        FormsModule,
        DropdownModule,
        ChartModule,
        TableModule,
        RatingModule,
        CalendarModule,
        ReactiveFormsModule,
        ToastModule,
        ProgressSpinnerModule,
        DividerModule,
        FieldsetModule,
        CardModule,
        PanelModule,
        CarouselModule,
        StyleClassModule,
        RippleModule
    ]
})
export class HomeModule {
}
