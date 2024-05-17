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
import {TaskDeadlineListComponent} from './components/task-deadline-list/task-deadline-list.component';
import {TaskDeadlineComponent} from './components/task-deadline/task-deadline.component';
import {TaskDeadlineCardComponent} from './components/task-deadline-card/task-deadline-card.component';
import {TaskCalendarComponent} from './components/task-calendar/task-calendar.component';
import {ViewTaskComponent} from './components/view-task/view-task.component';
import {SidebarModule} from "primeng/sidebar";
import {InplaceModule} from "primeng/inplace";
import {OverlayPanelModule} from "primeng/overlaypanel";
import {RippleModule} from "primeng/ripple";
import {DockModule} from "primeng/dock";
import {ReactiveFormsModule} from "@angular/forms";
import {ChipsModule} from "primeng/chips";
import {MultiSelectModule} from "primeng/multiselect";
import {FileUploadModule} from "primeng/fileupload";
import {SharedModule} from "../../shared/shared.module";
import {ToastModule} from "primeng/toast";
import {TabMenuModule} from "primeng/tabmenu";


@NgModule({
    declarations: [
        NewTaskComponent,
        EditTaskComponent,
        TaskListComponent,
        TaskDeadlineListComponent,
        TaskDeadlineComponent,
        TaskDeadlineCardComponent,
        TaskCalendarComponent,
        ViewTaskComponent
    ],
    exports: [
        NewTaskComponent,
        EditTaskComponent
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
        SidebarModule,
        InplaceModule,
        OverlayPanelModule,
        RippleModule,
        DockModule,
        ReactiveFormsModule,
        ChipsModule,
        MultiSelectModule,
        FileUploadModule,
        SharedModule,
        ToastModule,
        TabMenuModule,
    ]
})
export class TaskModule {
}
