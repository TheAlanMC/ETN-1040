import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListComponent } from './components/user-list/user-list.component';
import { NewUserComponent } from './components/new-user/new-user.component';
import { EditUserComponent } from './components/edit-user/edit-user.component';
import { ProfileComponent } from './components/profile/profile.component';
import { GroupAndRoleComponent } from './components/group-and-role/group-and-role.component';
import {UserRoutingModule} from "./user-routing.module";



@NgModule({
  declarations: [
    UserListComponent,
    NewUserComponent,
    EditUserComponent,
    ProfileComponent,
    GroupAndRoleComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule
  ]
})
export class UserModule { }
