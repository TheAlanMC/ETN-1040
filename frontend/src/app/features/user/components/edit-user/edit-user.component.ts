import {Component, OnInit, ViewChild} from '@angular/core';
import {UserDto} from "../../models/user.dto";
import {FormControl, Validators} from "@angular/forms";
import {FileUpload} from "primeng/fileupload";
import {ActivatedRoute, Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {UserService} from "../../../../core/services/user.service";
import {GroupService} from "../../../../core/services/group.service";
import {Location} from "@angular/common";

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrl: './edit-user.component.scss',
  providers: [MessageService]
})
export class EditUserComponent implements OnInit {

  userId: number = 0;

  profilePictureUrl: string = 'assets/layout/images/avatar.png';
  backupProfilePictureUrl: string = 'assets/layout/images/avatar.png';

  roles: string[] = [];
  groups: string[] = [];

  email = '';

  firstNameControl = new FormControl('', [Validators.required]);
  lastNameControl = new FormControl('', [Validators.required]);
  phoneControl = new FormControl('');
  descriptionControl = new FormControl('');

  changeProfilePicture = false;

  user: UserDto | null = null;

  @ViewChild('fileUpload') fileUpload!: FileUpload;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private groupService: GroupService, private router: Router, private messageService: MessageService, private location: Location) {
  }

  ngOnInit() {
    this.userId = this.activatedRoute.snapshot.params['id'];
    this.getUserInfo();
    this.getUserProfilePictureUrl();
  }

  public getUserProfilePictureUrl() {
    this.userService.getUserProfilePicture(this.userId).subscribe({
      next: (data) => {
        this.profilePictureUrl = URL.createObjectURL(data);
        this.backupProfilePictureUrl = URL.createObjectURL(data);
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public getUserInfo() {
    this.userService.getUser(this.userId).subscribe({
      next: (data) => {
        this.user = data.data;
        this.firstNameControl.setValue(this.user?.firstName ?? '');
        this.lastNameControl.setValue(this.user?.lastName ?? '');
        this.phoneControl.setValue(this.user?.phone ?? '');
        this.descriptionControl.setValue(this.user?.description ?? '');
        this.groups = this.user?.groups ?? [];
        this.roles = this.user?.roles ?? [];
        this.email = this.user?.email ?? '';
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onSelect(event: any) {
    this.changeProfilePicture = true
    this.profilePictureUrl = URL.createObjectURL(event.files[0]);
  }

  public onCancelSelect() {
    this.changeProfilePicture = false;
    this.fileUpload.clear();
    this.profilePictureUrl = this.backupProfilePictureUrl;
  }

  public onSave() {
    this.onUpload();
    this.userService.updateUser(
      this.userId,
      this.firstNameControl.value!,
      this.lastNameControl.value!,
      this.phoneControl.value!,
      this.descriptionControl.value!
    ).subscribe({
      next: (data) => {
        this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Usuario actualizado'});
        setTimeout(() => {
          this.router.navigate(['/users']).then(r => {
            console.log('Redirect to users page');
            window.location.reload();
          });
        }, 1000);
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    })
  }

  public onUpload() {
    if (this.changeProfilePicture) {
      this.userService.uploadUserProfilePicture(this.userId, this.fileUpload.files[0]).subscribe({
        next: (data) => {
          this.backupProfilePictureUrl = this.profilePictureUrl;
          this.onCancelSelect();
        },
        error: (error) => {
          console.log(error);
        }
      });
    }
  }

  public onCancel() {
    this.location.back();
  }
}
