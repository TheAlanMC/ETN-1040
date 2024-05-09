import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { NewProjectComponent } from './components/new-project/new-project.component';
import { EditProjectComponent } from './components/edit-project/edit-project.component';
import {ProjectRoutingModule} from "./project-routing.module";
import {DialogModule} from "primeng/dialog";
import {FullCalendarModule} from "@fullcalendar/angular";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CalendarModule} from "primeng/calendar";
import {DropdownModule} from "primeng/dropdown";
import {ChipsModule} from "primeng/chips";
import {InputTextareaModule} from "primeng/inputtextarea";
import {EditorModule} from "primeng/editor";
import {FileUploadModule} from "primeng/fileupload";
import {ChipModule} from "primeng/chip";
import {InputSwitchModule} from "primeng/inputswitch";
import {RippleModule} from "primeng/ripple";
import {MultiSelectModule} from "primeng/multiselect";
import {ToastModule} from "primeng/toast";



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
        InputTextareaModule,
        EditorModule,
        FileUploadModule,
        ChipModule,
        InputSwitchModule,
        RippleModule,
        ReactiveFormsModule,
        MultiSelectModule,
        ToastModule
    ]
})
export class ProjectModule { }
