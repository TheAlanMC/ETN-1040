import {Component, OnInit} from '@angular/core';
import {TaskDto} from "../../models/task.dto";
import {environment} from "../../../../../environments/environment";
import {TaskStatusDto} from "../../models/task-status.dto";
import {ConfirmationService, MessageService, SelectItem} from "primeng/api";
import {debounceTime, Subject} from "rxjs";
import {SharedService} from "../../../../core/services/shared.service";
import {ActivatedRoute} from "@angular/router";
import {TaskService} from "../../../../core/services/task.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {TaskPriorityDto} from "../../models/task-priority.dto";
import {UtilService} from "../../../../core/services/util.service";

@Component({
    selector: 'app-task-list',
    templateUrl: './task-list.component.html',
    styleUrl: './task-list.component.scss',
    providers: [
        ConfirmationService,
        MessageService,
    ],
})
export class TaskListComponent implements OnInit {

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

    statuses: TaskStatusDto[] = [];

    statusItems: SelectItem[] = [];

    selectedStatus: any[] = [];

    priorities: TaskPriorityDto[] = [];

    priorityItems: SelectItem[] = [];

    selectedPriority: any[] = [];

    keyword: string = '';

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
        private sharedService: SharedService,
        private activatedRoute: ActivatedRoute,
        private taskService: TaskService,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.roles.includes('CREAR TAREAS')) {
                this.canAddTask = true;
                this.isModerator = true
            }
            if (decoded.roles.includes('EDITAR TAREAS')) {
                this.canEditTask = true;
                this.isModerator = true
            }
            this.userId = decoded.userId;
            this.isMember = true;
        }
        this.keyword = this.sharedService.getData('keyword') ?? '';
        this.selectedStatus = this.sharedService.getData('selectedStatus') ?? [];
        this.selectedPriority = this.sharedService.getData('selectedPriority') ?? [];
    }

    ngOnInit() {
        this.activatedRoute.parent?.params.subscribe(params => {
            this.getAllPriorities();
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
        this.taskService.getTasks(this.sortBy,
            this.sortType,
            this.page,
            this.size,
            this.keyword,
            this.selectedStatus.map(status => status.label),
            this.selectedPriority.map(priority => priority.label),
        ).subscribe({
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

    public deleteTask(taskId: number) {
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

    public getAllPriorities() {
        this.taskService.getPriorities().subscribe({
            next: (data) => {
                this.priorities = data.data!;
                this.priorityItems = this.priorities.map(priority => {
                    return {
                        label: priority.taskPriorityName, value: priority.taskPriorityId
                    }
                });
                this.selectedPriority = this.selectedPriority.length == 0 ? this.priorityItems : this.selectedPriority;
            }, error: (error) => {
                console.error(error);
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
                    label: 'ATRASADO', value: 4
                });
                this.selectedStatus = this.selectedStatus.length == 0 ? this.statusItems.filter(status => (status.value === 1 || status.value === 2)) : this.selectedStatus;
                this.getData();
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onStatusChange(event: any) {
        this.selectedStatus = event.value;
        this.sharedService.changeData('selectedStatus',
            this.selectedStatus);
        this.getData();
    }

    public onPriorityChange(event: any) {
        this.selectedPriority = event.value;
        this.sharedService.changeData('selectedPriority',
            this.selectedPriority);
        this.getData();
    }

    public getPriorityColor(
        priorityId: number): string {
        let color = [0, 0, 0];
        switch (priorityId) {
            case 1:
                color = [0, 128, 0];
                break;
            case 2:
                color = [255, 165, 0];
                break;
            case 3:
                color = [255, 0, 0];
                break;
        }
        return `rgb(${color[0]}, ${color[1]}, ${color[2]},0.7)`;
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
