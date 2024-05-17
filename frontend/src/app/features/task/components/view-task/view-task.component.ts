import {Component, OnInit} from '@angular/core';
import {MenuItem} from "primeng/api";

@Component({
    selector: 'app-view-task',
    templateUrl: './view-task.component.html',
    styleUrl: './view-task.component.scss'
})
export class ViewTaskComponent implements OnInit {
    routeItems: MenuItem[] = [];

    ngOnInit() {
        this.routeItems = [
            {label: 'Proyecto', routerLink: 'detail'},
            {label: 'Tareas', routerLink: 'task-list'},
            {label: 'Plazos', routerLink: 'task-deadline'},
            {label: 'Calendario', routerLink: 'task-calendar'}];
    }
}

