import {Component, OnInit} from '@angular/core';
import {MenuItem} from "primeng/api";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
    selector: 'app-view-schedule',
    templateUrl: './view-schedule.component.html',
    styleUrl: './view-schedule.component.scss'
})
export class ViewScheduleComponent implements OnInit {

    canView = false;
    canCreate = false;
    canEdit = false;

    routeItems: MenuItem[] = [];

    ngOnInit() {
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            this.canView = decoded.permissions.includes('VER HORARIOS');
            this.canCreate = decoded.permissions.includes('CREAR HORARIOS');
            this.canEdit = decoded.permissions.includes('EDITAR HORARIOS');
        }
        this.routeItems = [
            {label: 'Horario', routerLink: 'schedule'},
            {label: 'Semestre', routerLink: 'semester'}
        ];

        this.routeItems = this.routeItems.filter(item => {
            if (item.routerLink === 'schedule') {
                return this.canView;
            } else if (item.routerLink === 'semester') {
                return this.canCreate || this.canEdit;
            }
            return false;
        });
    }
}

