import {Component, OnInit, ViewChild} from '@angular/core';
import {UserDto} from "../../models/user.dto";
import {FormControl, Validators} from "@angular/forms";
import {FileUpload} from "primeng/fileupload";
import {Router} from "@angular/router";
import {UserService} from "../../../../core/services/user.service";
import {GroupService} from "../../../../core/services/group.service";
import {MessageService, SelectItem} from "primeng/api";
import {Location} from '@angular/common';

@Component({
  selector: 'app-new-user',
  templateUrl: './new-user.component.html',
  styleUrl: './new-user.component.scss',
  providers: [MessageService]
})
export class NewUserComponent implements OnInit {
  selectedGroupId: number = 0;

  roles: string[] = [];
  selectedGroup: SelectItem = { value: '' };
  groups: SelectItem[] = [];

  loadingRoles = false;

  firstNameControl = new FormControl('', [Validators.required]);
  lastNameControl = new FormControl('', [Validators.required]);
  emailControl = new FormControl('', [Validators.required, Validators.email]);
  phoneControl = new FormControl('');
  descriptionControl = new FormControl('');

  user: UserDto | null = null;

  @ViewChild('fileUpload') fileUpload!: FileUpload;

  constructor(private userService: UserService, private groupService: GroupService, private router: Router, private messageService: MessageService, private location: Location) {
  }

  ngOnInit() {
    this.getGroups();
  }

  public getGroups() {
    this.groupService.getGroups().subscribe({
      next: (data) => {
        this.groups = data.data!!.map(group => {
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

  public onSelectGroup(event: any) {
    this.loadingRoles = true;
    this.groupService.getGroupRoles(event.value).subscribe({
      next: (data) => {
        this.roles = data.data!!.map(role => role.roleName);
        this.selectedGroupId = event.value;
        this.loadingRoles = false;
      },
      error: (error) => {
        console.log(error);
        this.loadingRoles = false;
      }
    });
  }

  public onClearGroup() {
    this.roles = [];
  }

  public onSave() {
    this.userService.createUser(
      this.selectedGroupId,
      this.emailControl.value!,
      this.firstNameControl.value!,
      this.lastNameControl.value!,
      this.phoneControl.value!,
      this.descriptionControl.value!
    ).subscribe({
      next: (data) => {
        this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Usuario creado'});
        setTimeout(() => {
          this.router.navigate(['/users']).then(r => console.log('Redirect to users page'));
        }, 1000);
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    })
  }

  public onCancel() {
    this.location.back();
  }
}
