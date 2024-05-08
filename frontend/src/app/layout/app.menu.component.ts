import { Component } from '@angular/core';
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
    role: string;
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
          role: 'VER DASHBOARD',
          routerLink: ['/dashboard']
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
          role: 'VER PROYECTOS',
          routerLink: ['/projects']
        },
        {
          label: 'Crear Proyecto',
          icon: 'pi pi-fw pi-plus',
          role: 'CREAR PROYECTOS',
          routerLink: ['/projects/create']
        },
        // {
        //   label: 'Editar Proyecto',
        //   icon: 'pi pi-fw pi-pencil',
        //   role: 'EDITAR PROYECTOS',
        //   routerLink: ['/projects/edit']
        // }
      ]
    },
    {
      label: 'Tareas por Proyecto',
      icon: 'pi pi-fw pi-check-square',
      items: [
        {
          label: 'Ver Tareas',
          icon: 'pi pi-fw pi-eye',
          role: 'VER TAREAS',
          routerLink: ['/tasks']
        },
        {
          label: 'Crear Tarea',
          icon: 'pi pi-fw pi-plus',
          role: 'CREAR TAREAS',
          routerLink: ['/tasks/create']
        },
        // {
        //   label: 'Editar Tareas',
        //   icon: 'pi pi-fw pi-pencil',
        //   role: 'EDITAR TAREAS',
        //   routerLink: ['/tasks/edit']
        // }
      ]
    },
    {
      label: 'Reportes',
      icon: 'pi pi-fw pi-chart-bar',
      items: [
        {
          label: 'Reportes de Proyectos',
          icon: 'pi pi-fw pi-chart-line',
          role: 'VER REPORTES DE PROYECTOS',
          routerLink: ['/reports/projects']
        },
        {
          label: 'Reportes de Tareas',
          icon: 'pi pi-fw pi-file',
          role: 'VER REPORTES DE TAREAS',
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
          role: 'VER USUARIOS',
          routerLink: ['/users']
        },
        {
          label: 'Crear Usuario',
          icon: 'pi pi-fw pi-user-plus',
          role: 'CREAR USUARIOS',
          routerLink: ['/users/create']
        },
        // {
        //   label: 'Editar Usuario',
        //   icon: 'pi pi-fw pi-user-edit',
        //   role: 'EDITAR USUARIOS',
        //   routerLink: ['/users/edit']
        // },
        {
          label: 'Roles y Permisos',
          icon: 'pi pi-fw pi-key',
          role: 'GESTIONAR ROLES Y PERMISOS',
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
        const decoded = jwtDecode<JwtPayload>(token!!);
        // Extract roles from token
        const roles = decoded.roles;
        // Filter menu items based on roles
        this.model = this.model.map(item => {
          if (item.items) {
            item.items = item.items.filter(subItem => {
              if (subItem.role) {
                return roles.includes(subItem.role);
              }
              return true;
            });
          }
          return item;
        }).filter(item => item.items && item.items.length > 0);
      }
    }
}
