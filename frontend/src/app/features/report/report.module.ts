import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ProjectReportComponent} from './components/project-report/project-report.component';
import {TaskReportComponent} from './components/task-report/task-report.component';
import {ReportRoutingModule} from "./report-routing.module";
import { ExecutiveReportComponent } from './components/executive-report/executive-report.component';
import { ReportListComponent } from './components/report-list/report-list.component';
import {ButtonModule} from "primeng/button";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {TableModule} from "primeng/table";
import {ToastModule} from "primeng/toast";
import {CalendarModule} from "primeng/calendar";
import {DockModule} from "primeng/dock";
import {SharedModule} from "../../shared/shared.module";
import {RippleModule} from "primeng/ripple";
import {RatingModule} from "primeng/rating";


@NgModule({
    declarations: [
        ProjectReportComponent,
        TaskReportComponent,
        ExecutiveReportComponent,
        ReportListComponent
    ], imports: [
        CommonModule,
        ReportRoutingModule,
        ButtonModule,
        ConfirmDialogModule,
        FormsModule,
        InputTextModule,
        SharedModule,
        TableModule,
        ToastModule,
        CalendarModule,
        ReactiveFormsModule,
        DockModule,
        RippleModule,
        RatingModule,
    ]
})
export class ReportModule {
}
