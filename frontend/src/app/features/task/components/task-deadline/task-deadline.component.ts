import {Component, OnInit} from '@angular/core';
import {TaskListDto} from "../../models/task-list.dto";
import {TaskDto} from "../../models/task.dto";
import {TaskStatusDto} from "../../models/task-status.dto";
import {MessageService, SelectItem} from "primeng/api";
import {environment} from "../../../../../environments/environment";
import {debounceTime, Subject} from "rxjs";
import {SharedService} from "../../../../core/services/shared.service";
import {ActivatedRoute} from "@angular/router";
import {TaskService} from "../../../../core/services/task.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {UserDto} from "../../../user/models/user.dto";
import {TaskPriorityDto} from "../../models/task-priority.dto";

@Component({
    selector: 'app-task-deadline',
    templateUrl: './task-deadline.component.html',
    styleUrl: './task-deadline.component.scss',
    providers: [MessageService],
})
export class TaskDeadlineComponent implements OnInit {

    taskLists: TaskListDto[] = [
        {
            listId: '1', title: 'Atrasado', tasks: []
        },
        {
            listId: '2', title: 'Para hoy', tasks: []
        },
        {
            listId: '3', title: 'Para esta semana', tasks: []
        },
        {
            listId: '4', title: 'Para la próxima semana', tasks: []
        },
        {
            listId: '5', title: 'Para más de una semana', tasks: []
        },
    ];

    taskListIds: string[] = this.taskLists.map(list => list.listId);


    isLoading: boolean = true;

    tasks: TaskDto[] = [];

    statuses: TaskStatusDto[] = [];

    statusItems: SelectItem[] = [];

    selectedStatus: any[] = [];

    priorities: TaskPriorityDto[] = [];

    priorityItems: SelectItem[] = [];

    selectedPriority: any[] = [];

    keyword: string = '';

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};
    userId: number = 0;
    canAddTask: boolean = false;
    isModerator: boolean = false;
    createSidebarVisible: boolean = false;
    editSidebarVisible: boolean = false;
    viewSidebarVisible: boolean = false;
    taskId: number = 0;

    private searchSubject = new Subject<string>();

    constructor(
        private sharedService: SharedService,
        private activatedRoute: ActivatedRoute,
        private taskService: TaskService,
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.permissions.includes('CREAR TAREAS')) {
                this.canAddTask = true;
                this.isModerator = true;
                this.sharedService.changeData('isModerator',
                    true);
            }

            this.userId = decoded.userId;
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

    public onSearch(event: any) {
        this.keyword = event.target.value;
        this.sharedService.changeData('keyword',
            this.keyword);
        this.searchSubject.next(this.keyword);
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


    public getData() {
        this.isLoading = true;
        this.taskService.getTasks('taskDueDate',
            'asc',
            0,
            1000,
            this.keyword,
            this.selectedStatus.map(status => status.label),
            this.selectedPriority.map(priority => priority.label)).subscribe({
            next: (data: ResponseDto<PageDto<TaskDto>>) => {
                this.tasks = data.data!.content;
                this.tasks.forEach(task => {
                    task.taskAssignees.forEach((assignee: UserDto) => this.fetchUserImage(assignee.userId));
                });
                // Add a new task to the list according to the deadline
                this.taskLists.forEach(list => {
                    let start = new Date();
                    let end = new Date();
                    list.tasks = this.tasks.filter(task => {
                        if (list.listId === '1') {
                            return task.taskDueDate && new Date(task.taskDueDate) < new Date();
                        } else if (list.listId === '2') {
                            let start = new Date();
                            let end = new Date(new Date().setHours(23,
                                59,
                                59,
                                999));
                            return task.taskDueDate && new Date(task.taskDueDate) >= new Date(start) && new Date(task.taskDueDate) < new Date(end);
                        } else if (list.listId === '3') {
                            let start = new Date(new Date().setDate(new Date().getDate() + 1));
                            start.setHours(0,
                                0,
                                0,
                                0);
                            let end = new Date(new Date().setDate(new Date().getDate() + 6));
                            end.setHours(23,
                                59,
                                59,
                                999);
                            return task.taskDueDate && new Date(task.taskDueDate) >= new Date(start) && new Date(task.taskDueDate) < new Date(end);
                        } else if (list.listId === '4') {
                            let start = new Date(new Date().setDate(new Date().getDate() + 7));
                            start.setHours(0,
                                0,
                                0,
                                0);
                            let end = new Date(new Date().setDate(new Date().getDate() + 13));
                            end.setHours(23,
                                59,
                                59,
                                999);
                            return task.taskDueDate && new Date(task.taskDueDate) >= new Date(start) && new Date(task.taskDueDate) < new Date(end);
                        } else if (list.listId === '5') {
                            let start = new Date(new Date().setDate(new Date().getDate() + 14));
                            start.setHours(0,
                                0,
                                0,
                                0);
                            return task.taskDueDate && new Date(task.taskDueDate) > new Date(start);
                        } else {
                            return false;
                        }
                    });
                });

                this.isLoading = false;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onViewCard(event: any) {
        this.taskId = event.taskId
        this.viewSidebarVisible = true;
    }

    public onEditCard(event: any) {
        this.taskId = event.taskId;
        this.editSidebarVisible = true;
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

    public fetchUserImage(userId: number) {
        const img = new Image();
        img.src = this.baseUrl + '/' + userId + '/profile-picture/thumbnail';
        img.onload = () => this.imgLoaded[userId] = true;
        img.onerror = () => this.imgLoaded[userId] = false;
    }

    public navigateToCreateTask() {
        this.createSidebarVisible = true
    }
}
