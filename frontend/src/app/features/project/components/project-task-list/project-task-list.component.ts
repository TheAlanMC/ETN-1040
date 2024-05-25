import {Component, OnInit} from '@angular/core';
import {ProjectDto} from "../../models/project.dto";
import {environment} from "../../../../../environments/environment";
import {ActivatedRoute} from "@angular/router";
import {ConfirmationService, MessageService, SelectItem} from "primeng/api";
import {UtilService} from "../../../../core/services/util.service";
import {ProjectService} from "../../../../core/services/project.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {TaskDto} from "../../../task/models/task.dto";
import {SharedService} from "../../../../core/services/shared.service";
import {TaskService} from "../../../../core/services/task.service";
import {TaskStatusDto} from "../../../task/models/task-status.dto";
import {debounceTime, Subject} from "rxjs";

@Component({
    selector: 'app-project-task-list',
    templateUrl: './project-task-list.component.html',
    styleUrl: './project-task-list.component.scss',
    providers: [ConfirmationService, MessageService],
})
export class ProjectTaskListComponent implements OnInit {

    tasks: TaskDto[] = [];

    // Pagination variables
    sortBy: string = 'taskId';
    sortType: string = 'desc';
    page: number = 0;
    size: number = 10;

    totalElements: number = 0;

    canAddTask: boolean = false;
    canEditTask: boolean = false;

    isLoading: boolean = true;

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};

    userId: number = 0;

    projectId: number = 0;

    project: ProjectDto | null = null;

    statuses: TaskStatusDto[] = [];

    statusItems: SelectItem[] = [];

    selectedStatus: any[] = [];

    keyword: string = '';

    isOwner: boolean = false;

    isModerator: boolean = false;

    isMember: boolean = false;

    createSidebarVisible: boolean = false;

    editSidebarVisible: boolean = false;

    viewSidebarVisible: boolean = false;

    taskId: number = 0;

    private searchSubject = new Subject<string>();

    constructor(
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private utilService: UtilService,
        private projectService: ProjectService,
        private sharedService: SharedService,
        private activatedRoute: ActivatedRoute,
        private taskService: TaskService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.roles.includes('CREAR TAREAS')) {
                this.canAddTask = true;
            }
            if (decoded.roles.includes('EDITAR TAREAS')) {
                this.canEditTask = true;
            }
            this.userId = decoded.userId;
        }
        this.keyword = this.sharedService.getData('keyword') ?? '';
        this.selectedStatus = this.sharedService.getData('selectedStatus') ?? [];
    }

    ngOnInit() {
        this.activatedRoute.parent?.params.subscribe(params => {
            this.projectId = params['id'];
            this.sharedService.changeData('projectId',
                this.projectId);
            this.getProjectInfo();
            this.getAllStatuses();
            this.searchSubject.pipe(debounceTime(500)).subscribe(() => {
                this.getData()
            });
        });
    }

    public navigateToCreateTask() {
        this.createSidebarVisible = true;
    }

    public navigateToViewTask(taskId: number) {
        this.viewSidebarVisible = true;
        this.taskId = taskId;
    }

    public navigateToEditTask(taskId: number) {
        this.editSidebarVisible = true;
        this.taskId = taskId;
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

    public onSearch(event: any) {
        this.keyword = event.target.value;
        this.sharedService.changeData('keyword',
            this.keyword);
        this.searchSubject.next(this.keyword);
    }

    public getData() {
        this.isLoading = true;
        this.projectService.getProjectTasks(this.projectId,
            this.sortBy,
            this.sortType,
            this.page,
            this.size,
            this.keyword,
            this.selectedStatus.map(status => status.label)).subscribe({
            next: (data: ResponseDto<PageDto<TaskDto>>) => {
                this.tasks = data.data!.content;
                this.totalElements = data.data!.page.totalElements;
                this.tasks.forEach(task => {
                    task.taskAssignees.forEach(assignee => this.fetchUserImage(assignee.userId));
                });
                this.isLoading = false;
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

    public onDeleteTask(taskId: number) {
        this.confirmationService.confirm({
            key: 'confirmDeleteTask',
            message: '¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.deleteTask(taskId);
            },
        });
    }

    public deleteTask(taskId: number): void {
        this.taskService.deleteTask(taskId).subscribe({
            next: (data) => {
                this.getData();
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Tarea eliminada correctamente'
                });
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public getProjectInfo() {
        this.projectService.getProject(this.projectId).subscribe({
            next: (data) => {
                this.project = data.data!;
                this.isOwner = this.project.projectOwners.find(owner => owner.userId === this.userId) != null;
                this.isModerator = this.project.projectModerators.find(moderator => moderator.userId === this.userId) != null;
                this.isMember = this.project.projectMembers.find(member => member.userId === this.userId) != null;
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public getAllStatuses() {
        this.taskService.getStatuses().subscribe({
            next: (data) => {
                this.statuses = data.data!;
                this.statusItems = this.statuses.map(status => {
                    return {
                        label: status.taskStatusName, value: status.taskStatusId
                    }
                });
                this.statusItems.push({
                    label: 'VENCIDO', value: 4
                });
                this.selectedStatus = this.selectedStatus.length == 0 ? this.statusItems.filter(status => (status.value === 1 || status.value === 2)) : this.selectedStatus;
                this.getData();
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public onStatusChange(event: any) {
        this.selectedStatus = event.value;
        this.sharedService.changeData('selectedStatus',
            this.selectedStatus);
        this.getData();
    }

    public getPriorityColor(
        priority: number,
        maxPriority: number = 10
    ): string {
        // Define the color ranges
        const colorRanges = [{
            min: 1, max: Math.round(maxPriority * 0.2), start: [0, 0, 255], end: [0, 128, 0]
        }, // Blue to Green
            {
                min: Math.round(maxPriority * 0.2) + 1,
                max: Math.round(maxPriority * 0.4),
                start: [0, 128, 0],
                end: [255, 255, 0]
            }, // Green to Yellow
            {
                min: Math.round(maxPriority * 0.4) + 1,
                max: Math.round(maxPriority * 0.6),
                start: [255, 255, 0],
                end: [255, 165, 0]
            }, // Yellow to Orange
            {
                min: Math.round(maxPriority * 0.6) + 1,
                max: Math.round(maxPriority * 0.8),
                start: [255, 165, 0],
                end: [255, 0, 0]
            }, // Orange to Red
            {
                min: Math.round(maxPriority * 0.8) + 1, max: maxPriority, start: [255, 0, 0], end: [128, 0, 0]
            }, // Red to Dark Red
        ];
        // Find the color range that the priority falls into
        const range = colorRanges.find(r => priority >= r.min && priority <= r.max);
        if (!range) {
            return '#000000'; // Return black if no range is found (should not happen)
        }
        // Calculate the ratio of where the priority falls within the range
        const ratio = (priority - range.min) / (range.max - range.min);
        // Interpolate the color
        const color = range.start.map((
            start,
            i
        ) => Math.round(start + ratio * (range.end[i] - start)));
        // Convert the color to a CSS RGB string
        return `rgb(${color[0]}, ${color[1]}, ${color[2]}, 0.5)`;
    }

    public getStatusColor(statusId: number): string {
        let color = [0, 0, 0];
        switch (statusId) {
            case 1:
                color = [255, 165, 0];
                break;
            case 2:
                color = [0, 128, 0];
                break;
            case 3:
                color = [0, 0, 255];
                break;
        }
        return `rgb(${color[0]}, ${color[1]}, ${color[2]},0.7)`;
    }

    public checkIfTaskIsOverdue(
        statusId: number,
        taskDueDate: Date
    ): boolean {
        if (statusId === 3) {
            return false;
        }
        return new Date(taskDueDate).getTime() < new Date().getTime();
    }
}
