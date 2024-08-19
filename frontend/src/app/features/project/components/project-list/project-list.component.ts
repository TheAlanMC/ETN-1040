import {Component, OnInit} from '@angular/core';
import {ProjectDto} from "../../models/project.dto";
import {environment} from "../../../../../environments/environment";
import {Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {ProjectService} from '../../../../core/services/project.service';
import {debounceTime, Subject} from "rxjs";
import {SharedService} from "../../../../core/services/shared.service";

@Component({
    selector: 'app-project-list',
    templateUrl: './project-list.component.html',
    styleUrl: './project-list.component.scss',
    providers: [
        ConfirmationService,
        MessageService
    ],
})
export class ProjectListComponent implements OnInit {

    projects: ProjectDto[] = [];

    // Pagination variables
    sortBy: string = 'projectId';
    sortType: string = 'desc';
    page: number = 0;
    size: number = 10;

    totalElements: number = 0;

    canAddProject: boolean = false;
    canEditProject: boolean = false;

    isDataLoading: boolean = true;

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};

    keyword: string = '';

    private searchSubject = new Subject<string>();

    constructor(
        private router: Router,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private projectService: ProjectService,
        private sharedService: SharedService,
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.permissions.includes('CREAR PROYECTOS')) {
                this.canAddProject = true;
            }
            if (decoded.permissions.includes('EDITAR PROYECTOS')) {
                this.canEditProject = true;
            }
        }
    }

    ngOnInit() {
        this.getData();
        this.searchSubject.pipe(debounceTime(500)).subscribe(() => {
            this.getData()
        });
    }

    public onSearch(event: any) {
        this.keyword = event.target.value;
        this.searchSubject.next(this.keyword);
    }

    public navigateToCreateProject() {
        this.router.navigate(['/projects/create']).then(r => console.log('Navigate to create project'));
    }

    public navigateToViewProject(projectId: number) {
        this.sharedService.changeData('selectedStatus',
            [])
        this.sharedService.changeData('selectedPriority',
            [])
        this.sharedService.changeData('projectEndDate',
            this.projects.find(project => project.projectId === projectId)?.projectEndDate)
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
    }

    public getData() {
        this.isDataLoading = true;
        this.projectService.getProjects(this.sortBy,
            this.sortType,
            this.page,
            this.size,
            this.keyword
        ).subscribe({
            next: (data: ResponseDto<PageDto<ProjectDto>>) => {
                this.projects = data.data!.content;
                this.totalElements = data.data!.page.totalElements;
                this.projects.forEach(project => {
                    project.projectOwners.forEach(user => this.fetchUserImage(user.userId));
                    project.projectModerators.forEach(user => this.fetchUserImage(user.userId));
                    project.projectMembers.forEach(user => this.fetchUserImage(user.userId));
                });
                this.isDataLoading = false;
            }, error: (error) => {
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

    public deleteProject(projectId: number) {
        this.projectService.deleteProject(projectId).subscribe({
            next: (data) => {
                this.getData();
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Proyecto eliminado correctamente'
                });
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

}
