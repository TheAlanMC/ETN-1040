import {Component, OnInit} from '@angular/core';
import {ProjectDto} from "../../models/project.dto";
import {environment} from "../../../../../environments/environment";
import {Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {UtilService} from "../../../../core/services/util.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {ProjectService} from '../../../../core/services/project.service';
import {UserService} from "../../../../core/services/user.service";
import {UserDto} from "../../../user/models/user.dto";

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class ProjectListComponent implements OnInit {

  projects: ProjectDto[] = [];

  // Pagination variables
  sortBy: string = 'projectId';
  sortType: string = 'asc';
  page: number = 0;
  size: number = 10;

  totalElements: number = 0;

  canAddProject: boolean = false;
  canEditProject: boolean = false;

  isLoading: boolean = true;

  baseUrl: string = `${environment.API_URL}/api/v1/users`;

  imgLoaded: { [key: string]: boolean } = {};

  users: UserDto[] = [];

  constructor(private router: Router, private confirmationService: ConfirmationService, private messageService: MessageService, private utilService: UtilService, private projectService: ProjectService, private userService: UserService) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    // Get token from local storage
    const token = localStorage.getItem('token');
    if (token) {
      const decoded = jwtDecode<JwtPayload>(token!!);
      if (decoded.roles.includes('CREAR PROYECTOS')) {
        this.canAddProject = true;
      }
      if (decoded.roles.includes('EDITAR PROYECTOS')) {
        this.canEditProject = true;
      }
    }
  }

  ngOnInit() {
    this.getAllUsers();
    this.getData();
  }

  public navigateToCreateProject() {
    this.router.navigate(['/projects/create']).then(r => console.log('Navigate to create project'));
  }

  public navigateToViewProject(projectId: number) {
    this.router.navigate(['/projects/view/' + projectId]).then(r => console.log('Navigate to view project'));
  }

  public navigateToEditProject(projectId: number) {
    this.router.navigate(['/projects/edit/' + projectId]).then(r => console.log('Navigate to edit project'));
  }

  public onPageChange(event: any) {
    const first = event.first;
    const rows = event.rows;
    this.page = Math.floor(first / rows);
    this.size = rows;
    this.getData();
  }

  public onSortChange(event: any) {
    this.sortBy = event.field;
    this.sortType = (event.order == 1) ? 'asc' : 'desc';
    this.getData();
    console.log(event);
  }

  public getData() {
    this.isLoading = true;
    this.projectService.getProjects(this.sortBy, this.sortType, this.page, this.size).subscribe({
      next: (data: ResponseDto<PageDto<ProjectDto>>) => {
        this.projects = data.data!.content;
        this.totalElements = data.data!.page.totalElements;
        this.projects.forEach(project => {
            project.projectOwnerIds.forEach(userId => this.fetchUserImage(userId));
            project.projectModeratorIds.forEach(userId => this.fetchUserImage(userId));
            project.projectMemberIds.forEach(userId => this.fetchUserImage(userId));
          }
        );
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }


  public fetchUserImage(userId: number) {
    const img = new Image();
    img.src = this.baseUrl + '/' + userId + '/profile-picture/thumbnail';
    img.onload = () => this.imgLoaded[userId] = true;
    img.onerror = () => this.imgLoaded[userId] = false;
  }

  public onDeleteProject(projectId: number) {
    this.confirmationService.confirm({
      key: 'confirmDeleteProject',
      message: '¿Estás seguro de que deseas eliminar este proyecto? Esta acción no se puede deshacer.',
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.deleteProject(projectId);
      },
    });
  }

  public deleteProject(projectId: number): void {
    this.projectService.deleteProject(projectId).subscribe({
      next: (data) => {
        this.getData();
        this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Proyecto eliminado correctamente'});
      },
      error: (error) => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'No se pudo eliminar el proyecto'});
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

  public getImageLoaded(userId: any): boolean {
    return this.imgLoaded[userId] ?? false;
  }

  public setImageLoaded(userId: any, value: boolean) {
    this.imgLoaded[userId] = value;
  }

  public getFullName(userId: any): string {
    const user = this.users.find(user => user.userId === userId);
    return `${user?.firstName} ${user?.lastName}`;
  }

  public getEmail(userId: any): string {
    const user = this.users.find(user => user.userId === userId);
    return user?.email ?? '';
  }

}
