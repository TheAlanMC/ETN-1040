import {Component, OnInit} from '@angular/core';
import {MenuItem} from "primeng/api";

@Component({
    selector: 'app-view-project',
    templateUrl: './view-project.component.html',
    styleUrl: './view-project.component.scss',
})
export class ViewProjectComponent implements OnInit {

    routeItems: MenuItem[] = [];

    ngOnInit() {
        this.routeItems = [{label: 'Proyecto', routerLink: 'detail'}, {
            label: 'Tareas', routerLink: 'task-list'
        }, {label: 'Plazos', routerLink: 'task-deadline'}, {label: 'Calendario', routerLink: 'task-calendar'}];
    }
}
