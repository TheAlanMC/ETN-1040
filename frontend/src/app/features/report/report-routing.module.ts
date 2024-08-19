import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PermissionGuard} from "../../core/guards/permission.guard";
import {ProjectReportComponent} from "./components/project-report/project-report.component";
import {TaskReportComponent} from "./components/task-report/task-report.component";
import {ExecutiveReportComponent} from "./components/executive-report/executive-report.component";
import {ReportListComponent} from "./components/report-list/report-list.component";


const routes: Routes = [
    {
        path: 'reports', children: [
            {
                path: 'list',
                component: ReportListComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER REPORTES GENERADOS'], breadcrumb: 'Lista'}
            },
            {
                path: 'executive',
                component: ExecutiveReportComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER REPORTES EJECUTIVOS'], breadcrumb: 'Ejecutivos'}
            },
            {
                path: 'projects',
                component: ProjectReportComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER REPORTES DE PROYECTOS'], breadcrumb: 'Proyectos'}
            },
            {
                path: 'tasks',
                component: TaskReportComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER REPORTES DE TAREAS'], breadcrumb: 'Tareas'}
            },
            {path: '', redirectTo: 'projects', pathMatch: 'full'}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)], exports: [RouterModule]
})
export class ReportRoutingModule {
}
