import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { NewProjectComponent } from './components/new-project/new-project.component';
import { EditProjectComponent } from './components/edit-project/edit-project.component';
import {ProjectRoutingModule} from "./project-routing.module";
import {DialogModule} from "primeng/dialog";
import {FullCalendarModule} from "@fullcalendar/angular";
import {FormsModule} from "@angular/forms";
import {CalendarModule} from "primeng/calendar";
import {DropdownModule} from "primeng/dropdown";
import {ChipsModule} from "primeng/chips";
import {InputTextareaModule} from "primeng/inputtextarea";



@NgModule({
  declarations: [
    ProjectListComponent,
    NewProjectComponent,
    EditProjectComponent,
  ],
  imports: [
    CommonModule,
    ProjectRoutingModule,
    DialogModule,
    FullCalendarModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
    ChipsModule,
    InputTextareaModule
  ]
})
export class ProjectModule { }
