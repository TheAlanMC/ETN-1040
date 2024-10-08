import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {PermissionGuard} from "../../core/guards/permission.guard";
import {TaskListComponent} from "./components/task-list/task-list.component";
import {TaskCalendarComponent} from "./components/task-calendar/task-calendar.component";
import {TaskDeadlineComponent} from "./components/task-deadline/task-deadline.component";
import {ViewAssignedTaskComponent} from "./components/view-assigned-task/view-assigned-task.component";


const routes: Routes = [
    {
        path: 'tasks/view',
        component: ViewAssignedTaskComponent,
        canActivate: [PermissionGuard],
        data: {permissions: ['VER TAREAS'], breadcrumb: 'Ver'},
        children: [
            {path: 'list', component: TaskListComponent, data: {breadcrumb: 'Tareas'}},
            {path: 'deadline', component: TaskDeadlineComponent, data: {breadcrumb: 'Plazos'}},
            {path: 'calendar', component: TaskCalendarComponent, data: {breadcrumb: 'Calendario'}},
            {path: '', redirectTo: 'list', pathMatch: 'full'}
        ]
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)], exports: [RouterModule]
})
export class TaskRoutingModule {
}
