import {Component, OnInit} from '@angular/core';
import {ConfirmationService, MessageService, SelectItem} from "primeng/api";
import {UserService} from "../../../../core/services/user.service";
import {RoleService} from "../../../../core/services/role.service";
import {RoleDto} from "../../models/role.dto";
import {PermissionService} from "../../../../core/services/permission.service";
import {PermissionDto} from '../../models/permission.dto';
import {FormControl, Validators} from "@angular/forms";
import {environment} from "../../../../../environments/environment";
import {UserDto} from "../../models/user.dto";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
    selector: 'app-group-and-role',
    templateUrl: './group-and-role.component.html',
    styleUrl: './group-and-role.component.scss',
    providers: [MessageService, ConfirmationService,],
})
export class GroupAndRoleComponent implements OnInit {

    isLoading: boolean = false;

    selectedUserId = 0;
    selectedRoleId = 0;

    selectedUser: SelectItem = {value: ''};
    selectedRole: SelectItem = {value: ''};

    userItems: SelectItem[] = [];
    roleItems: SelectItem[] = [];

    roles: RoleDto[] = [];

    permissions: PermissionDto[] = [];

    sourceRoles: any[] = [{name: 'Seleccione un usuario', code: ''}];
    targetRoles: any[] = [{name: 'Seleccione un usuario', code: ''}];

    sourcePermissions: any[] = [{name: 'Seleccione un rol', code: ''}];
    targetPermissions: any[] = [{name: 'Seleccione un rol', code: ''}];

    isPickListDisabled = true;

    visibleAddRole = false
    visibleEditRole = false

    roleNameControl = new FormControl('',
        [Validators.required]);
    roleDescriptionControl = new FormControl('',
        [Validators.required]);

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};
    userId: number = 0;
    users: UserDto[] = [];

    constructor(
        private userService: UserService,
        private confirmationService: ConfirmationService,
        private roleService: RoleService,
        private permissionService: PermissionService,
        private messageService: MessageService,
    ) {
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            this.userId = decoded.userId;
        }
    }

    ngOnInit() {
        this.getRoles();
        this.getPermissions();
        this.getAllUsers();
    }

    public getAllUsers() {
        this.userService.getAllUsers().subscribe({
            next: (data) => {
                this.users = data.data!;
                this.userItems = data.data!.map(user => {
                    // Pre-fetch the image
                    const img = new Image();
                    img.src = this.baseUrl + '/' + user.userId + '/profile-picture/thumbnail';
                    img.onload = () => this.imgLoaded[user.userId] = true;
                    img.onerror = () => this.imgLoaded[user.userId] = false;
                    return {
                        label: `${user.firstName} ${user.lastName}`,
                        labelSecondary: user.email,
                        value: user.userId,
                        disabled: (user.userId === this.userId)
                    }
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public getRoles() {
        this.roleService.getRoles().subscribe({
            next: (data) => {
                this.roles = data.data!;
                this.roleItems = data.data!.map(role => {
                    return {
                        label: role.roleName, value: role.roleId
                    }
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public getPermissions() {
        this.permissionService.getPermissions().subscribe({
            next: (data) => {
                this.permissions = data.data!;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onSelectUser(event: any) {
        if (event.value == null) {
            return
        }
        this.userService.getUserRoles(event.value).subscribe({
            next: (data) => {
                this.selectedUserId = event.value;
                this.targetRoles = data.data!.map(role => {
                    return {
                        name: role.roleName, code: role.roleId
                    }
                });
                this.sourceRoles = this.roles.filter(role => data.data!.findIndex(userGroup => userGroup.roleId === role.roleId) === -1).map(role => {
                    return {
                        name: role.roleName, code: role.roleId
                    }
                });
                this.isPickListDisabled = false;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onClearUser() {
        this.selectedUserId = 0;
        this.selectedUser = {value: ''};
        this.sourceRoles = [{name: 'Seleccione un usuario', code: ''}];
        this.targetRoles = [{name: 'Seleccione un usuario', code: ''}];
    }

    public saveUserRoles() {
        this.isLoading = true;
        this.userService.addUsersToRole(this.selectedUserId,
            this.targetRoles.map(role => role.code)).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Permisos asignados correctamente'
                });
                this.isLoading = false;
                this.selectedUser = {value: ''};
                this.onClearUser();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public sortSourceRoles() {
        this.sourceRoles.sort((
            a,
            b
        ) => a.code - b.code);
    }

    public sortTargetRoles() {
        this.targetRoles.sort((
            a,
            b
        ) => a.code - b.code);
    }

    public onDeleteUser() {
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

    public deleteUser() {
        this.userService.deleteUser(this.selectedUserId).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Usuario eliminado correctamente'
                });
                this.selectedUser = {value: ''};
                this.onClearUser();
                this.getAllUsers();
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onSelectPermission(event: any) {
        if (event.value == null) {
            return
        }
        this.roleService.getRolePermissions(event.value).subscribe({
            next: (data) => {
                this.selectedRoleId = event.value;
                this.targetPermissions = data.data!.map(permission => {
                    return {
                        name: permission.permissionName, code: permission.permissionId
                    }
                });
                this.sourcePermissions = this.permissions.filter(permission => data.data!.findIndex(userRole => userRole.permissionId === permission.permissionId) === -1).map(permission => {
                    return {
                        name: permission.permissionName, code: permission.permissionId
                    }
                });
                this.isPickListDisabled = false;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onClearRole() {
        this.selectedRole = {value: ''};
        this.selectedRoleId = 0;
        this.sourcePermissions = [{name: 'Seleccione un rol', code: ''}];
        this.targetPermissions = [{name: 'Seleccione un rol', code: ''}];
    }

    public saveRolePermissions() {
        this.isLoading = true;
        this.roleService.addPermissionsToRole(this.selectedRoleId,
            this.targetPermissions.map(permission => permission.code)).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Permisos asignados correctamente'
                });
                this.isLoading = false;
                this.onClearRole();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public sortSourcePermissions() {
        this.sourcePermissions.sort((
            a,
            b
        ) => a.code - b.code);
    }

    public sortTargetPermission() {
        this.targetPermissions.sort((
            a,
            b
        ) => a.code - b.code);
    }

    public onDeleteRole() {
        this.confirmationService.confirm({
            key: 'confirmDeleteRole',
            message: '¿Estás seguro de que deseas eliminar este rol? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.deleteRole();
            },
        });
    }

    public deleteRole() {
        this.roleService.deleteRole(this.selectedRoleId).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Rol eliminado correctamente'
                });
                this.onClearRole();
                this.getRoles();
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onAddRole() {
        this.visibleAddRole = true;
        this.roleNameControl.setValue('');
        this.roleDescriptionControl.setValue('');
    }

    public onEditRole() {
        this.visibleEditRole = true;
        this.roleNameControl.setValue(this.roles.find(role => role.roleId === this.selectedRoleId)!.roleName);
        this.roleDescriptionControl.setValue(this.roles.find(role => role.roleId === this.selectedRoleId)!.roleDescription);
    }

    public onSaveRole() {
        this.isLoading = true;
        this.roleService.createRole(this.roleNameControl.value!,
            this.roleDescriptionControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Rol creado correctamente'
                });
                this.isLoading = false;
                this.visibleAddRole = false;
                this.getRoles();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onUpdateRole() {
        this.isLoading = true;
        this.roleService.updateRole(this.selectedRoleId,
            this.roleNameControl.value!,
            this.roleDescriptionControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Rol actualizado correctamente'
                });
                this.isLoading = false;
                this.visibleEditRole = false;
                this.getRoles();
                this.onClearRole();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onTabChange(event: any) {
        if (event.index === 0) {
            this.onClearUser();
        } else if (event.index === 1) {
            this.onClearRole();
        }
    }
}
