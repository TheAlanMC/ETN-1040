import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ProjectReportComponent} from './components/project-report/project-report.component';
import {TaskReportComponent} from './components/task-report/task-report.component';
import {ReportRoutingModule} from "./report-routing.module";


@NgModule({
    declarations: [
        ProjectReportComponent,
        TaskReportComponent
    ], imports: [
        CommonModule,
        ReportRoutingModule
    ]
})
export class ReportModule {
}
