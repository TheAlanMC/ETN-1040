import {Component, OnInit} from '@angular/core';
import {ConfirmationService, MessageService, SelectItem} from "primeng/api";
import {UserService} from "../../../../core/services/user.service";
import {GroupService} from "../../../../core/services/group.service";
import {GroupDto} from "../../models/group.dto";
import {RoleService} from "../../../../core/services/role.service";
import { RoleDto } from '../../models/role.dto';
import {FormControl, Validators} from "@angular/forms";

@Component({
  selector: 'app-group-and-role',
  templateUrl: './group-and-role.component.html',
  styleUrl: './group-and-role.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class GroupAndRoleComponent implements OnInit{

  selectedUserId = 0;
  selectedGroupId = 0;

  selectedUser: SelectItem = { value: '' };
  selectedGroup: SelectItem = { value: '' };

  userItems: SelectItem[] = [];
  groupItems: SelectItem[] = [];

  groups: GroupDto[] = [];

  roles: RoleDto[] = [];

  sourceGroups: any[] = [
    { name: 'Seleccione un usuario', code: '' }
  ];
  targetGroups: any[] = [
    { name: 'Seleccione un usuario', code: '' }
  ];

  sourceRoles: any[] = [
    { name: 'Seleccione un rol', code: '' }
  ];
  targetRoles: any[] = [
    { name: 'Seleccione un rol', code: '' }
  ];

  isPickListDisabled = true;

  visibleAddGroup = false
  visibleEditGroup = false

  groupNameControl = new FormControl('', [Validators.required]);
  groupDescriptionControl = new FormControl('', [Validators.required]);

  constructor(private userService: UserService, private confirmationService: ConfirmationService, private groupsService: GroupService, private rolseService: RoleService, private messageService: MessageService) {
  }

  ngOnInit() {
    this.getGroups();
    this.getRoles();
    this.getAllUsers();
  }

  public getAllUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.userItems = data.data!!.map(user => {
          return {
            label: user.email,
            value: user.userId,
          }
        });
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public getGroups() {
    this.groupsService.getGroups().subscribe({
      next: (data) => {
        this.groups = data.data!!;
        this.groupItems = data.data!!.map(group => {
          return {
            label: group.groupName,
            value: group.groupId
          }
        });
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public getRoles() {
    this.rolseService.getRoles().subscribe({
      next: (data) => {
        this.roles = data.data!!;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onSelectUser(event: any) {
    if (event.value == null){
      return
    }
    this.userService.getUserGroups(event.value).subscribe({
      next: (data) => {
        this.selectedUserId = event.value;
        this.targetGroups = data.data!!.map(group => {
          return {
            name: group.groupName,
            code: group.groupId
          }
        });
        this.sourceGroups = this.groups.filter(
          group => data.data!!.findIndex(userGroup => userGroup.groupId === group.groupId) === -1
        ).map(group => {
          return {
            name: group.groupName,
            code: group.groupId
          }
        });
        this.isPickListDisabled = false;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onClearUser() {
    this.selectedUserId = 0;
    this.sourceGroups = [
      { name: 'Seleccione un usuario', code: '' }
    ];
    this.targetGroups = [
      { name: 'Seleccione un usuario', code: '' }
    ];
  }

  public saveUserGroups() {
    this.userService.addUsersToGroup(this.selectedUserId, this.targetGroups.map(group => group.code)).subscribe({
      next: (data) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Grupos asignados correctamente'
        });
        this.selectedUser = { value: '' };
        this.onClearUser();
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error al asignar los grupos'});
      }
    });
  }

  public sortSourceGroups() {
    this.sourceGroups.sort((a, b) => a.code - b.code);
  }

  public sortTargetGroups() {
    this.targetGroups.sort((a, b) => a.code - b.code);
  }

  public onDeleteUser(){
    this.confirmationService.confirm({
      key: 'confirmDeleteUser',
      message: '¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.',
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.deleteUser();
      },
    });
  }

  public deleteUser(){
    this.userService.deleteUser(this.selectedUserId).subscribe({
      next: (data) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Usuario eliminado correctamente'
        });
        this.selectedUser = { value: '' };
        this.onClearUser();
        this.getAllUsers();
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    });
  }

  public onSelectRole(event: any) {
    if (event.value == null){
      return
    }
    this.groupsService.getGroupRoles(event.value).subscribe({
      next: (data) => {
        this.selectedGroupId = event.value;
        this.targetRoles = data.data!!.map(role => {
          return {
            name: role.roleName,
            code: role.roleId
          }
        });
        this.sourceRoles = this.roles.filter(
          role => data.data!!.findIndex(userRole => userRole.roleId === role.roleId) === -1
        ).map(role => {
          return {
            name: role.roleName,
            code: role.roleId
          }
        });
        this.isPickListDisabled = false;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onClearGroup() {
    this.selectedGroup = { value: '' };
    this.selectedGroupId = 0;
    this.sourceRoles = [
      { name: 'Seleccione un rol', code: '' }
    ];
    this.targetRoles = [
      { name: 'Seleccione un rol', code: '' }
    ];
  }

  public saveGroupRoles() {
    this.groupsService.addRolesToGroup(this.selectedGroupId, this.targetRoles.map(role => role.code)).subscribe({
      next: (data) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Roles asignados correctamente'
        });
        this.onClearGroup();
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error al asignar los roles'});
      }
    });
  }

  public sortSourceRoles() {
    this.sourceRoles.sort((a, b) => a.code - b.code);
  }

  public sortTargetRoles() {
    this.targetRoles.sort((a, b) => a.code - b.code);
  }

  public onDeleteGroup(){
    this.confirmationService.confirm({
      key: 'confirmDeleteGroup',
      message: '¿Estás seguro de que deseas eliminar este rol? Esta acción no se puede deshacer.',
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.deleteGroup();
      },
    });
  }

  public deleteGroup(){
    this.groupsService.deleteGroup(this.selectedGroupId).subscribe({
      next: (data) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Grupo eliminado correctamente'
        });
        this.onClearGroup();
        this.getGroups();
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    });
  }

  public onAddGroup(){
    this.visibleAddGroup = true;
    this.groupNameControl.setValue('');
    this.groupDescriptionControl.setValue('');
  }

  public onEditGroup(){
    this.visibleEditGroup = true;
    this.groupNameControl.setValue(this.groups.find(group => group.groupId === this.selectedGroupId)!!.groupName);
    this.groupDescriptionControl.setValue(this.groups.find(group => group.groupId === this.selectedGroupId)!!.groupDescription);
  }

  onSaveGroup(){
    this.groupsService.createGroup(this.groupNameControl.value!, this.groupDescriptionControl.value!).subscribe({
      next: (data) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Grupo creado correctamente'
        });
        this.visibleAddGroup = false;
        this.getGroups();
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    });
  }

  public onUpdateGroup(){
    this.groupsService.updateGroup(this.selectedGroupId, this.groupNameControl.value!!, this.groupDescriptionControl.value!!).subscribe({
      next: (data) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Grupo actualizado correctamente'
        });
        this.visibleEditGroup = false;
        this.getGroups();
        this.onClearGroup();
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    });
  }
}
