import {Component, OnInit} from '@angular/core';
import {environment} from "../../../../../environments/environment";
import {UserDto} from "../../../user/models/user.dto";
import {SelectItem} from "primeng/api";
import {ProjectDto} from "../../models/project.dto";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../../core/services/user.service";
import {UtilService} from "../../../../core/services/util.service";
import {Location} from "@angular/common";
import {ProjectService} from "../../../../core/services/project.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.scss'
})
export class ProjectDetailComponent implements OnInit {
  projectId: number = 0;

  editorModules = {
    toolbar: [
      [{'header': [1, 2, false]}],
      ['bold', 'italic', 'underline'],
      [{'color': []}, {'background': []}],
      [{'list': 'ordered'}, {'list': 'bullet'}, {'align': []}],
      [{indent: '-1'}, {indent: '+1'}],
    ]
  };

  baseUrl: string = `${environment.API_URL}/api/v1/users`;
  imgLoaded: { [key: string]: boolean } = {};
  userId: number = 0;
  users: UserDto[] = [];
  userItems: SelectItem[] = [];
  // selectedProjectManagers: any[] = [];
  selectedModerators: any[] = [];
  selectedMembers: any[] = [];

  project: ProjectDto | null = null;

  dateFrom: string = '';
  dateTo: string = '';

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private utilService: UtilService, private location: Location, private projectService: ProjectService, private router: Router) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    const token = localStorage.getItem('token');
    // Check if token exists
    if (token) {
      const decoded = jwtDecode<JwtPayload>(token!!);
      this.userId = decoded.userId;
    }
  }

  ngOnInit() {
    this.activatedRoute.parent?.params.subscribe(params => {
      this.projectId = params['id'];
      this.getAllUsers();
    });
    // this.projectId = this.activatedRoute.snapshot.params['id'];
    // this.getAllUsers();
    this.getProjectInfo();
  }

  public getProjectInfo() {
    this.projectService.getProject(this.projectId).subscribe({
      next: (data) => {
        this.project = data.data!!;
        this.dateFrom = new Date(data.data!!.dateFrom).toLocaleDateString('en-GB')
        this.dateTo = new Date(data.data!!.dateTo).toLocaleDateString('en-GB')
        this.selectedMembers = data.data!!.projectMemberIds.map(member => {
          const user = this.users.find(u => u.userId === member);
          return {
            label: `${user!!.firstName} ${user!!.lastName}`,
            labelSecondary: user!!.email,
            value: user!!.userId,
            disabled: (user!!.userId === this.userId)
          }
        });
        this.selectedModerators = data.data!!.projectModeratorIds.map(moderator => {
          const user = this.users.find(u => u.userId === moderator);
          return {
            label: `${user!!.firstName} ${user!!.lastName}`,
            labelSecondary: user!!.email,
            value: user!!.userId,
            disabled: (user!!.userId === this.userId)
          }
        });
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public getAllUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data.data!!;
        this.userItems = data.data!!.map(user => {
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
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onBack() {
    this.location.back();
  }
}
