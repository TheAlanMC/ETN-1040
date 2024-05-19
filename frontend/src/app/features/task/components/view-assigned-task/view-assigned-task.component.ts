import {Component, OnInit} from '@angular/core';
import {MenuItem} from "primeng/api";

@Component({
    selector: 'app-view-assigned-task',
    templateUrl: './view-assigned-task.component.html',
    styleUrl: './view-assigned-task.component.scss',
})
export class ViewAssignedTaskComponent implements OnInit {
    routeItems: MenuItem[] = [];

    ngOnInit() {
        this.routeItems = [{label: 'Tareas', routerLink: 'list'}, {label: 'Plazos', routerLink: 'deadline'},
            {label: 'Calendario', routerLink: 'calendar'}];
    }
}
