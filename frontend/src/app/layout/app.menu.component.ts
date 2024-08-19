import {Component} from '@angular/core';
import {JwtPayload} from "../core/models/jwt-payload.dto";
import {jwtDecode} from "jwt-decode";
import {Router} from "@angular/router";

interface MenuItem {
    label: string;
    icon: string;
    items: SubMenuItem[];
    separator?: boolean;
}

interface SubMenuItem {
    label: string;
    icon: string;
    permission: string;
    routerLink: string[];
}

@Component({
    selector: 'app-menu',
    templateUrl: './app.menu.component.html'
})
export class AppMenuComponent {

    model: MenuItem[] = [
        {
            label: 'Dashboard',
            icon: 'pi pi-home',
            items: [
                {
                    label: 'Ver Dashboard',
                    icon: 'pi pi-fw pi-home',
                    permission: 'VER DASHBOARD',
                    routerLink: ['/dashboard']
                },
            ]
        },
        {
          label: 'Horario',
            icon: 'pi pi-fw pi-calendar',
            items: [
                {
                    label: 'Ver Horario',
                    icon: 'pi pi-fw pi-list',
                    permission: 'VER HORARIOS',
                    routerLink: ['/schedule/manage/schedule']
                },
                {
                    label: 'Semestre',
                    icon: 'pi pi-fw pi-calendar-plus',
                        permission: 'CREAR HORARIOS',
                    routerLink: ['/schedule/manage/semester']
                },
            ]
        },
        {
            label: 'Proyectos',
            icon: 'pi pi-fw pi-briefcase',
            items: [
                {
                    label: 'Todos los Proyectos',
                    icon: 'pi pi-fw pi-list',
                    permission: 'VER PROYECTOS',
                    routerLink: ['/projects']
                },
                {
                    label: 'Crear Proyecto',
                    icon: 'pi pi-fw pi-plus',
                    permission: 'CREAR PROYECTOS',
                    routerLink: ['/projects/create']
                },
            ]
        },
        {
            label: 'Tareas Asignadas',
            icon: 'pi pi-fw pi-check-square',
            items: [
                {
                    label: 'Ver Lista de Tareas',
                    icon: 'pi pi-fw pi-list',
                    permission: 'VER TAREAS',
                    routerLink: ['/tasks/view/list']
                },
                {
                    label: 'Plazos de Tareas',
                    icon: 'pi pi-fw pi-clock',
                    permission: 'VER TAREAS',
                    routerLink: ['/tasks/view/deadline']
                },
                {
                    label: 'Calendario de Tareas',
                    icon: 'pi pi-fw pi-calendar',
                    permission: 'VER TAREAS',
                    routerLink: ['/tasks/view/calendar']
                }
            ]
        },
        {
            label: 'Reportes',
            icon: 'pi pi-fw pi-chart-bar',
            items: [
                {
                    label: 'Reportes Generados',
                    icon: 'pi pi-fw pi-chart-bar',
                    permission: 'VER REPORTES GENERADOS',
                    routerLink: ['/reports/list']
                },
                {
                    label: 'Reportes Ejecutivos',
                    icon: 'pi pi-fw pi-chart-line',
                    permission: 'VER REPORTES EJECUTIVOS',
                    routerLink: ['/reports/executive']
                },
                {
                    label: 'Reportes de Proyectos',
                    icon: 'pi pi-fw pi-file',
                    permission: 'VER REPORTES DE PROYECTOS',
                    routerLink: ['/reports/projects']
                },
                {
                    label: 'Reportes de Tareas',
                    icon: 'pi pi-fw pi-file',
                    permission: 'VER REPORTES DE TAREAS',
                    routerLink: ['/reports/tasks']
                }
            ]
        },
        {
            label: 'Gestión de Usuarios',
            icon: 'pi pi-fw pi-users',
            items: [
                {
                    label: 'Lista de Usuarios',
                    icon: 'pi pi-fw pi-user-edit',
                    permission: 'VER USUARIOS',
                    routerLink: ['/users']
                },
                {
                    label: 'Crear Usuario',
                    icon: 'pi pi-fw pi-user-plus',
                    permission: 'CREAR USUARIOS',
                    routerLink: ['/users/create']
                },
                {
                    label: 'Roles y Permisos',
                    icon: 'pi pi-fw pi-key',
                    permission: 'GESTIONAR ROLES Y PERMISOS',
                    routerLink: ['/users/management']
                }
            ]
        }
    ];

    constructor(private router: Router) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            // Extract permissions from token
            const permissions = decoded.permissions;
            // Filter menu items based on permissions
            this.model = this.model.map(item => {
                if (item.items) {
                    item.items = item.items.filter(subItem => {
                        if (subItem.permission) {
                            return permissions.includes(subItem.permission);
                        }
                        return true;
                    });
                }
                return item;
            }).filter(item => item.items && item.items.length > 0);
        }
    }
}
