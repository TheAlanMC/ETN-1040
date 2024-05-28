import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RoleGuard} from "../../core/guards/role.guard";
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
                canActivate: [RoleGuard],
                data: {roles: ['VER REPORTES GENERADOS'], breadcrumb: 'Lista'}
            },
            {
                path: 'executive',
                component: ExecutiveReportComponent,
                canActivate: [RoleGuard],
                data: {roles: ['VER REPORTES EJECUTIVOS'], breadcrumb: 'Ejecutivos'}
            },
            {
                path: 'projects',
                component: ProjectReportComponent,
                canActivate: [RoleGuard],
                data: {roles: ['VER REPORTES DE PROYECTOS'], breadcrumb: 'Proyectos'}
            },
            {
                path: 'tasks',
                component: TaskReportComponent,
                canActivate: [RoleGuard],
                data: {roles: ['VER REPORTES DE TAREAS'], breadcrumb: 'Tareas'}
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
