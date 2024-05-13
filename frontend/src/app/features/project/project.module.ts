import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ProjectListComponent} from './components/project-list/project-list.component';
import {NewProjectComponent} from './components/new-project/new-project.component';
import {EditProjectComponent} from './components/edit-project/edit-project.component';
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
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {TableModule} from "primeng/table";
import {AvatarGroupModule} from "primeng/avatargroup";
import {AvatarModule} from "primeng/avatar";
import {ViewProjectComponent} from './components/view-project/view-project.component';
import {DockModule} from "primeng/dock";
import {OverlayPanelModule} from "primeng/overlaypanel";
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {ProjectDetailComponent} from './components/project-detail/project-detail.component';
import {ProjectTaskListComponent} from './components/project-task-list/project-task-list.component';
import {ProjectTaskDeadlineComponent} from './components/project-task-deadline/project-task-deadline.component';
import {ProjectTaskCalendarComponent} from './components/project-task-calendar/project-task-calendar.component';
import {TabMenuModule} from "primeng/tabmenu";
import {StepsModule} from "primeng/steps";
import {TabViewModule} from "primeng/tabview";


@NgModule({
  declarations: [
    ProjectListComponent,
    NewProjectComponent,
    EditProjectComponent,
    ViewProjectComponent,
    ProjectDetailComponent,
    ProjectTaskListComponent,
    ProjectTaskDeadlineComponent,
    ProjectTaskCalendarComponent,
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
    ToastModule,
    ConfirmDialogModule,
    TableModule,
    AvatarGroupModule,
    AvatarModule,
    DockModule,
    OverlayPanelModule,
    ProgressSpinnerModule,
    TabMenuModule,
    StepsModule,
    TabViewModule
  ]
})
export class ProjectModule {
}
