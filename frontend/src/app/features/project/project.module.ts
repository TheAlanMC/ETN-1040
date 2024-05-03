import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { NewProjectComponent } from './components/new-project/new-project.component';
import { EditProjectComponent } from './components/edit-project/edit-project.component';
import {ProjectRoutingModule} from "./project-routing.module";



@NgModule({
  declarations: [
    ProjectListComponent,
    NewProjectComponent,
    EditProjectComponent,
  ],
  imports: [
    CommonModule,
    ProjectRoutingModule
  ]
})
export class ProjectModule { }
