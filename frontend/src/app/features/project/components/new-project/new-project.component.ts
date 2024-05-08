import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import { MessageService, PrimeNGConfig, SelectItem} from "primeng/api";
import {environment} from "../../../../../environments/environment";
import {UserDto} from "../../../user/models/user.dto";
import {UserService} from "../../../../core/services/user.service";
import {UtilService} from "../../../../core/services/util.service";
import {Location} from '@angular/common';
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
  selector: 'app-new-project',
  templateUrl: './new-project.component.html',
  styleUrl: './new-project.component.scss',
  providers: [ MessageService]
})
export class NewProjectComponent implements OnInit {

  editorModules = {
    toolbar: [
      [{ 'header': [1, 2, false] }],
      ['bold', 'italic', 'underline'],
      [{'color': []}, {'background': []}],
      [{'list': 'ordered'}, {'list': 'bullet'}, {'align': []}],
    ]
  };

  projectNameControl = new FormControl('', [Validators.required]);
  dateFromControl = new FormControl('', [Validators.required]);
  dateToControl = new FormControl('', [Validators.required]);
  projectDescriptionControl = new FormControl('');

  baseUrl: string = `${environment.API_URL}/api/v1/users`;
  imgLoaded: { [key: string]: boolean } = {};
  userId: number = 0;
  users: UserDto[] = [];
  userItems: SelectItem[] = [];
  // selectedProjectManagers: any[] = [];
  selectedCollaborators: any[] = [];
  selectedTeamMembers: any[] = [];


  constructor(private userService: UserService, private messageService: MessageService, private utilService: UtilService, private location: Location) {
    if (this.utilService.checkIfMobile()) {
      this.baseUrl = this.baseUrl.replace('/backend', ':8080');
    }
    const token = localStorage.getItem('token');
    // Check if token exists
    if (token) {
      const decoded = jwtDecode<JwtPayload>(token!!);
      this.userId = decoded.userId;
    }
  }

  ngOnInit() {
    this.getAllUsers();
  }

  public getAllUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data.data!!;
        this.userItems = data.data!!.map(user => {
          return {
            label: user.email,
            value: user.userId,
            disabled: (user.userId === this.userId)
          }
        });
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onCancel() {
    this.location.back();
  }
}
