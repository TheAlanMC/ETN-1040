import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProjectListComponent} from "./components/project-list/project-list.component";
import {EditProjectComponent} from "./components/edit-project/edit-project.component";
import {NewProjectComponent} from "./components/new-project/new-project.component";
import {PermissionGuard} from "../../core/guards/permission.guard";
import {ViewProjectComponent} from "./components/view-project/view-project.component";
import {ProjectDetailComponent} from "./components/project-detail/project-detail.component";
import {ProjectTaskListComponent} from "./components/project-task-list/project-task-list.component";
import {ProjectTaskDeadlineComponent} from "./components/project-task-deadline/project-task-deadline.component";
import {ProjectTaskCalendarComponent} from "./components/project-task-calendar/project-task-calendar.component";


const routes: Routes = [
    {
        path: 'projects', children: [
            {
                path: '',
                component: ProjectListComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER PROYECTOS']}
            },
            {
                path: 'view/:id',
                component: ViewProjectComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['VER PROYECTOS'], breadcrumb: 'Ver'},
                children: [
                    {path: 'detail', component: ProjectDetailComponent, data: {breadcrumb: 'Detalles'}},
                    {path: 'task-list', component: ProjectTaskListComponent, data: {breadcrumb: 'Tareas'}},
                    {path: 'task-deadline', component: ProjectTaskDeadlineComponent, data: {breadcrumb: 'Plazos'}},
                    {path: 'task-calendar', component: ProjectTaskCalendarComponent, data: {breadcrumb: 'Calendario'}},
                    {path: '', redirectTo: 'detail', pathMatch: 'full'}
                ]
            },
            {
                path: 'create',
                component: NewProjectComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['CREAR PROYECTOS'], breadcrumb: 'Nuevo'}
            },
            {
                path: 'edit/:id',
                component: EditProjectComponent,
                canActivate: [PermissionGuard],
                data: {permissions: ['EDITAR PROYECTOS'], breadcrumb: 'Editar'}
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)], exports: [RouterModule]
})
export class ProjectRoutingModule {
}
