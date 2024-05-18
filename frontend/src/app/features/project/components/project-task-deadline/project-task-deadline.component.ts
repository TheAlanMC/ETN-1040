import {Component, OnInit} from '@angular/core';
import {TaskListDto} from "../../../task/models/task-list.dto";
import {environment} from "../../../../../environments/environment";
import {TaskStatusDto} from "../../../task/models/task-status.dto";
import {MessageService, SelectItem} from "primeng/api";
import {debounceTime, Subject} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {UtilService} from "../../../../core/services/util.service";
import {ProjectService} from "../../../../core/services/project.service";
import {SharedService} from "../../../../core/services/shared.service";
import {TaskService} from "../../../../core/services/task.service";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {TaskDto} from "../../../task/models/task.dto";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ProjectDto} from "../../models/project.dto";
import {UserService} from "../../../../core/services/user.service";
import {UserDto} from "../../../user/models/user.dto";

@Component({
    selector: 'app-project-task-deadline',
    templateUrl: './project-task-deadline.component.html',
    styleUrl: './project-task-deadline.component.scss',
    providers: [MessageService]
})
export class ProjectTaskDeadlineComponent implements OnInit {

    taskLists: TaskListDto[] = [
        {
            listId: '1', title: 'Vencido', tasks: []
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

    projectId: number = 0;

    isLoading: boolean = true;

    tasks: TaskDto[] = [];

    statuses: TaskStatusDto[] = [];

    statusItems: SelectItem[] = [];

    selectedStatus: any[] = [];

    keyword: string = '';

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};
    userId: number = 0;
    canAddTask: boolean = false;
    project: ProjectDto | null = null;
    isOwner: boolean = false;
    isModerator: boolean = false;
    createSidebarVisible: boolean = false;
    editSidebarVisible: boolean = false;
    viewSidebarVisible: boolean = false;
    taskId: number = 0;

    private searchSubject = new Subject<string>();

    constructor(private projectService: ProjectService, private sharedService: SharedService, private activatedRoute: ActivatedRoute, private taskService: TaskService, private utilService: UtilService, private router: Router, private userService: UserService) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.roles.includes('CREAR TAREAS')) {
                this.canAddTask = true;
            }

            this.userId = decoded.userId;
        }
        this.keyword = this.sharedService.getData('keyword') ?? '';
        this.selectedStatus = this.sharedService.getData('selectedStatus') ?? [];
    }

    ngOnInit() {
        this.activatedRoute.parent?.params.subscribe(params => {
            this.projectId = params['id'];
            this.sharedService.changeData('projectId', this.projectId);
            this.getProjectInfo();
            this.getAllStatuses();
            this.searchSubject.pipe(debounceTime(500)).subscribe(() => {
                this.getData()
            });
        });
    }

    public onSearch(event: any) {
        this.keyword = event.target.value;
        this.sharedService.changeData('keyword', this.keyword);
        this.searchSubject.next(this.keyword);
    }

    public onStatusChange(event: any) {
        this.selectedStatus = event.value;
        this.sharedService.changeData('selectedStatus', this.selectedStatus);
        this.getData();
    }


    public getData() {
        this.isLoading = true;
        this.projectService.getProjectTasks(this.projectId, 'taskDeadline', 'asc', 0, 1000, this.keyword, this.selectedStatus.map(status => status.label)).subscribe({
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
                            return task.taskDeadline && new Date(task.taskDeadline) < new Date();
                        } else if (list.listId === '2') {
                            return task.taskDeadline && new Date(task.taskDeadline).toDateString() === new Date().toDateString();
                        } else if (list.listId === '3') {
                            let start = new Date(new Date().setDate(new Date().getDate() + 1));
                            start.setHours(0, 0, 0, 0);
                            let end = new Date(new Date().setDate(new Date().getDate() + 6));
                            end.setHours(23, 59, 59, 999);
                            return task.taskDeadline && new Date(task.taskDeadline) > new Date(start) && new Date(task.taskDeadline) < new Date(end);
                        } else if (list.listId === '4') {
                            let start = new Date(new Date().setDate(new Date().getDate() + 7));
                            start.setHours(0, 0, 0, 0);
                            let end = new Date(new Date().setDate(new Date().getDate() + 13));
                            end.setHours(23, 59, 59, 999);
                            return task.taskDeadline && new Date(task.taskDeadline) > new Date(start) && new Date(task.taskDeadline) < new Date(end);
                        } else if (list.listId === '5') {
                            let start = new Date(new Date().setDate(new Date().getDate() + 14));
                            start.setHours(0, 0, 0, 0);
                            return task.taskDeadline && new Date(task.taskDeadline) > new Date(start);
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

    public getProjectInfo() {
        this.projectService.getProject(this.projectId).subscribe({
            next: (data) => {
                this.project = data.data!;
                this.isOwner = this.project.projectOwners.find(owner => owner.userId === this.userId) != null;
                this.isModerator = this.project.projectModerators.find(moderator => moderator.userId === this.userId) != null;
                this.sharedService.changeData('isOwner', this.isOwner);
                this.sharedService.changeData('isModerator', this.isModerator);
            }, error: (error) => {
                console.log(error);
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
