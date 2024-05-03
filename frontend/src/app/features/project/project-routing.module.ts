import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProjectListComponent} from "./components/project-list/project-list.component";
import {EditProjectComponent} from "./components/edit-project/edit-project.component";
import {NewProjectComponent} from "./components/new-project/new-project.component";
import {RoleGuard} from "../../core/guards/role.guard";


const routes: Routes = [
  {path: 'projects',
    children: [
      {path: '', component: ProjectListComponent, canActivate: [RoleGuard], data: { roles: ['VER PROYECTOS']}},
      {path: 'create', component: NewProjectComponent, canActivate: [RoleGuard], data: { roles: ['CREAR PROYECTOS'],  breadcrumb: 'Nuevo'}},
      {path: 'edit/:id', component: EditProjectComponent, canActivate: [RoleGuard], data: { roles: ['EDITAR PROYECTOS'],  breadcrumb: 'Editar'}},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProjectRoutingModule { }
