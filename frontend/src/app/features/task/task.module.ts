import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NewTaskComponent} from './components/new-task/new-task.component';
import {EditTaskComponent} from './components/edit-task/edit-task.component';
import {TaskListComponent} from './components/task-list/task-list.component';
import {TaskRoutingModule} from "./task-routing.module";
import {ButtonModule} from "primeng/button";
import {CalendarModule} from "primeng/calendar";
import {DialogModule} from "primeng/dialog";
import {DropdownModule} from "primeng/dropdown";
import {FullCalendarModule} from "@fullcalendar/angular";
import {InputTextModule} from "primeng/inputtext";
import {InputTextareaModule} from "primeng/inputtextarea";
import {PaginatorModule} from "primeng/paginator";
import {SharedModule} from "primeng/api";


@NgModule({
  declarations: [
    NewTaskComponent,
    EditTaskComponent,
    TaskListComponent
  ],
  imports: [
    CommonModule,
    TaskRoutingModule,
    ButtonModule,
    CalendarModule,
    DialogModule,
    DropdownModule,
    FullCalendarModule,
    InputTextModule,
    InputTextareaModule,
    PaginatorModule,
    SharedModule
  ]
})
export class TaskModule {
}
