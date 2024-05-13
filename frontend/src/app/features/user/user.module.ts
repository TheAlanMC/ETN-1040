import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserListComponent} from './components/user-list/user-list.component';
import {NewUserComponent} from './components/new-user/new-user.component';
import {EditUserComponent} from './components/edit-user/edit-user.component';
import {ProfileComponent} from './components/profile/profile.component';
import {GroupAndRoleComponent} from './components/group-and-role/group-and-role.component';
import {UserRoutingModule} from "./user-routing.module";
import {FileUploadModule} from "primeng/fileupload";
import {InputTextareaModule} from "primeng/inputtextarea";
import {ChipsModule} from "primeng/chips";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {ToastModule} from "primeng/toast";
import {RippleModule} from "primeng/ripple";
import {SidebarModule} from "primeng/sidebar";
import {PasswordModule} from "primeng/password";
import {DialogModule} from "primeng/dialog";
import {TableModule} from "primeng/table";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {DropdownModule} from "primeng/dropdown";
import {TabViewModule} from "primeng/tabview";
import {PickListModule} from "primeng/picklist";
import {ViewUserComponent} from './components/view-user/view-user.component';
import {KeyFilterModule} from "primeng/keyfilter";

@NgModule({
  declarations: [
    UserListComponent,
    NewUserComponent,
    EditUserComponent,
    ProfileComponent,
    GroupAndRoleComponent,
    ViewUserComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    FileUploadModule,
    InputTextareaModule,
    ChipsModule,
    FormsModule,
    ReactiveFormsModule,
    ProgressSpinnerModule,
    ToastModule,
    RippleModule,
    SidebarModule,
    PasswordModule,
    DialogModule,
    TableModule,
    ConfirmDialogModule,
    DropdownModule,
    TabViewModule,
    PickListModule,
    KeyFilterModule,
  ]
})
export class UserModule {
}
