import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { SemesterComponent } from './components/semester/semester.component';
import {ScheduleRoutingModule} from "./schedule-routing.module";
import { ViewScheduleComponent } from './components/view-schedule/view-schedule.component';
import {TabMenuModule} from "primeng/tabmenu";
import {ButtonModule} from "primeng/button";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {DialogModule} from "primeng/dialog";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {InputTextareaModule} from "primeng/inputtextarea";
import {PickListModule} from "primeng/picklist";
import {RippleModule} from "primeng/ripple";
import {TabViewModule} from "primeng/tabview";
import {ToastModule} from "primeng/toast";
import {CalendarModule} from "primeng/calendar";
import {FullCalendarModule} from "@fullcalendar/angular";
import {FloatLabelModule} from "primeng/floatlabel";
import {MultiSelectModule} from "primeng/multiselect";
import {AvatarGroupModule} from "primeng/avatargroup";
import {AvatarModule} from "primeng/avatar";
import {OverlayPanelModule} from "primeng/overlaypanel";
import {SharedModule} from "../../shared/shared.module";
import {TagModule} from "primeng/tag";
import {TieredMenuModule} from "primeng/tieredmenu";



@NgModule({
  declarations: [
    ScheduleComponent,
    SemesterComponent,
    ViewScheduleComponent
  ],
    imports: [
        CommonModule,
        ScheduleRoutingModule,
        TabMenuModule,
        ButtonModule,
        ConfirmDialogModule,
        DialogModule,
        DropdownModule,
        FormsModule,
        InputTextModule,
        InputTextareaModule,
        PickListModule,
        RippleModule,
        TabViewModule,
        ToastModule,
        ReactiveFormsModule,
        CalendarModule,
        FullCalendarModule,
        FloatLabelModule,
        MultiSelectModule,
        AvatarGroupModule,
        AvatarModule,
        OverlayPanelModule,
        SharedModule,
        TagModule,
        TieredMenuModule
    ]
})
export class ScheduleModule { }
