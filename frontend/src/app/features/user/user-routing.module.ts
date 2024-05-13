import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {RoleGuard} from "../../core/guards/role.guard";
import {UserListComponent} from "./components/user-list/user-list.component";
import {NewUserComponent} from "./components/new-user/new-user.component";
import {EditUserComponent} from "./components/edit-user/edit-user.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {AuthGuard} from "../../core/guards/auth.guard";
import {GroupAndRoleComponent} from "./components/group-and-role/group-and-role.component";
import {ViewUserComponent} from "./components/view-user/view-user.component";


const routes: Routes = [
  {
    path: 'users',
    children: [
      {path: '', component: UserListComponent, canActivate: [RoleGuard], data: {roles: ['VER USUARIOS']}},
      {
        path: 'create',
        component: NewUserComponent,
        canActivate: [RoleGuard],
        data: {roles: ['CREAR USUARIOS'], breadcrumb: 'Nuevo'}
      },
      {
        path: 'view/:id',
        component: ViewUserComponent,
        canActivate: [RoleGuard],
        data: {roles: ['VER USUARIOS'], breadcrumb: 'Ver'}
      },
      {
        path: 'edit/:id',
        component: EditUserComponent,
        canActivate: [RoleGuard],
        data: {roles: ['EDITAR USUARIOS'], breadcrumb: 'Editar'}
      },
      {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard], data: {breadcrumb: 'Perfil'}},
      {
        path: 'management',
        component: GroupAndRoleComponent,
        canActivate: [RoleGuard],
        data: {roles: ['GESTIONAR ROLES Y PERMISOS'], breadcrumb: 'Roles y Permisos'}
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule {
}
