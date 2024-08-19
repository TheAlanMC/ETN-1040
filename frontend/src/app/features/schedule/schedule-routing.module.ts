import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PermissionGuard} from "../../core/guards/permission.guard";
import {ScheduleComponent} from "./components/schedule/schedule.component";
import {SemesterComponent} from "./components/semester/semester.component";
import {ViewScheduleComponent} from "./components/view-schedule/view-schedule.component";


const routes: Routes = [
    {
        path: 'schedule', children: [
            {
                path: 'manage',
                component: ViewScheduleComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER HORARIOS', 'CREAR HORARIOS', 'EDITAR HORARIOS'], breadcrumb: 'Gestionar'},
                children: [
                    {path: 'schedule', component: ScheduleComponent, data: {breadcrumb: 'Horario'}},
                    {path: 'semester', component: SemesterComponent, data: {breadcrumb: 'Semestre'}},
                    {path: '', redirectTo: '', pathMatch: 'full'}
                ]
            },
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)], exports: [RouterModule]
})
export class ScheduleRoutingModule {
}
