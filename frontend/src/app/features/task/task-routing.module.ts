import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {RoleGuard} from "../../core/guards/role.guard";
import {TaskListComponent} from "./components/task-list/task-list.component";
import {NewTaskComponent} from "./components/new-task/new-task.component";
import {EditTaskComponent} from "./components/edit-task/edit-task.component";


const routes: Routes = [
  {path: 'tasks',
    children: [
      {path: '', component: TaskListComponent, canActivate: [RoleGuard], data: { roles: ['VER TAREAS']}},
      {path: 'create', component: NewTaskComponent, canActivate: [RoleGuard], data: { roles: ['CREAR TAREAS'],  breadcrumb: 'Nuevo'}},
      {path: 'edit/:id', component: EditTaskComponent, canActivate: [RoleGuard], data: { roles: ['EDITAR TAREAS'],  breadcrumb: 'Editar'}},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TaskRoutingModule { }
