import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RoleGuard} from "../../core/guards/role.guard";
import {ProjectReportComponent} from "./components/project-report/project-report.component";
import {TaskReportComponent} from "./components/task-report/task-report.component";


const routes: Routes = [
    {
        path: 'reports', children: [
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
