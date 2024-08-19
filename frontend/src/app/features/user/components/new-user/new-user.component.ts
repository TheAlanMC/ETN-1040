import {Component, OnInit, ViewChild} from '@angular/core';
import {UserDto} from "../../models/user.dto";
import {FormControl, Validators} from "@angular/forms";
import {FileUpload} from "primeng/fileupload";
import {Router} from "@angular/router";
import {UserService} from "../../../../core/services/user.service";
import {RoleService} from "../../../../core/services/role.service";
import {MessageService, SelectItem} from "primeng/api";
import {Location} from '@angular/common';

@Component({
    selector: 'app-new-user',
    templateUrl: './new-user.component.html',
    styleUrl: './new-user.component.scss',
    providers: [MessageService],
})
export class NewUserComponent implements OnInit {

    isLoading: boolean = false;

    selectedRoleId: number = 0;

    permission: string[] = [];
    selectedRole: SelectItem = {value: ''};
    roles: SelectItem[] = [];

    loadingPermissions = false;

    firstNameControl = new FormControl('',
        [Validators.required]);
    lastNameControl = new FormControl('',
        [Validators.required]);
    emailControl = new FormControl('',
        [Validators.required, Validators.email]);
    phoneControl = new FormControl('');
    descriptionControl = new FormControl('');

    user: UserDto | null = null;

    @ViewChild('fileUpload') fileUpload!: FileUpload;

    constructor(
        private userService: UserService,
        private roleService: RoleService,
        private router: Router,
        private messageService: MessageService,
        private location: Location
    ) {
    }

    ngOnInit() {
        this.getRoles();
    }

    public getRoles() {
        this.roleService.getRoles().subscribe({
            next: (data) => {
                this.roles = data.data!.map(role => {
                    return {
                        label: role.roleName, value: role.roleId
                    }
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onSelectPermission(event: any) {
        this.loadingPermissions = true;
        this.roleService.getRolePermissions(event.value).subscribe({
            next: (data) => {
                this.permission = data.data!.map(permission => permission.permissionName);
                this.selectedRoleId = event.value;
                this.loadingPermissions = false;
            }, error: (error) => {
                console.error(error);
                this.loadingPermissions = false;
            }
        });
    }

    public onClearPermission() {
        this.permission = [];
    }

    public onSave() {
        this.isLoading = true;
        this.userService.createUser(this.selectedRoleId,
            this.emailControl.value!,
            this.firstNameControl.value!,
            this.lastNameControl.value!,
            this.phoneControl.value!,
            this.descriptionControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Usuario creado'});
                setTimeout(() => {
                        this.router.navigate(['/users']).then(r => console.log('Redirect to users page'));
                        this.isLoading = false;
                    },
                    500);
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        })
    }

    public onCancel() {
        this.location.back();
    }
}
