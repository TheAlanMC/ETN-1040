import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {ProjectDto} from "../../../project/models/project.dto";
import {UserService} from "../../../../core/services/user.service";
import {ProjectService} from "../../../../core/services/project.service";
import {TaskService} from "../../../../core/services/task.service";
import {MessageService, SelectItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import { UtilService } from '../../../../core/services/util.service';

@Component({
  selector: 'app-new-task',
  templateUrl: './new-task.component.html',
  styleUrl: './new-task.component.scss',
  providers: [MessageService]
})
export class NewTaskComponent implements OnInit {

  @Input() sidebarVisible: boolean = false;
  @Input() projectId: number = 0;
  @Output() sidebarVisibleChange = new EventEmitter<boolean>();


  taskNameControl = new FormControl('', [Validators.required]);
  taskDescriptionControl = new FormControl('');
  taskDeadlineControl = new FormControl('', [Validators.required]);
  taskPriorityControl = new FormControl('', [Validators.required]);

  priorities: SelectItem[] = [
    {label: 'Nivel 1', value: 1},
    {label: 'Nivel 2', value: 2},
    {label: 'Nivel 3', value: 3},
    {label: 'Nivel 4', value: 4},
    {label: 'Nivel 5', value: 5},
    {label: 'Nivel 6', value: 6},
    {label: 'Nivel 7', value: 7},
    {label: 'Nivel 8', value: 8},
    {label: 'Nivel 9', value: 9},
    {label: 'Nivel 10', value: 10},
  ];

  projects: ProjectDto[] = [];

  projectItems: SelectItem[] = [];

  users: UserDto[] = [];

  usersItems: SelectItem[] = [];
  selectedAssignees: any[] = [];

  selectedProject: any = null;

  baseUrl: string = `${environment.API_URL}/api/v1/users`;

  imgLoaded: { [key: string]: boolean } = {};



  constructor(private userService: UserService, private projectService: ProjectService, private taskService: TaskService, messageService: MessageService, private utilService: UtilService) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
  }

  ngOnInit() {
    this.getAllProjects();
    this.getAllUsers();
  }


  public getAllProjects() {
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        this.projects = data.data!!;
        this.projectItems = this.projects.map(project => {
          return {label: project.projectName, value: project.projectId};
        });
        if (this.projectId != 0){
          this.selectedProject = this.projects.find(p => p.projectId == this.projectId) || null;
          this.onProjectChange(null);
        }
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
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onProjectChange(event: any) {
    this.selectedAssignees = [];
    // Show only the users that are part of the selected project (project.projectMemberIds)
    if (this.selectedProject) {
      this.usersItems = this.users.filter(user => this.selectedProject.projectMemberIds.includes(user.userId)).map(user => {
        // Pre-fetch the image
        const img = new Image();
        img.src = this.baseUrl + '/' + user.userId + '/profile-picture/thumbnail';
        img.onload = () => this.imgLoaded[user.userId] = true;
        img.onerror = () => this.imgLoaded[user.userId] = false;
        return {
          label: `${user.firstName} ${user.lastName}`,
          labelSecondary: user.email,
          value: user.userId,
        }});
      this.selectedAssignees = [];
      }
    }


  onClose() {
    this.sidebarVisibleChange.emit(false);
    this.sidebarVisible = false;
    this.taskNameControl.reset();
    this.taskDescriptionControl.reset();
    this.taskDeadlineControl.reset();
    this.taskPriorityControl.reset();
  }
}
