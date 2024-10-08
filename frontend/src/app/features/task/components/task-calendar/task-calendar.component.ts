import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FullCalendarComponent} from "@fullcalendar/angular";
import {TaskDto} from "../../models/task.dto";
import {environment} from "../../../../../environments/environment";
import {TaskStatusDto} from "../../models/task-status.dto";
import {ConfirmationService, MenuItem, MessageService, SelectItem} from "primeng/api";
import {debounceTime, Subject} from "rxjs";
import {SharedService} from "../../../../core/services/shared.service";
import {ActivatedRoute} from "@angular/router";
import {TaskService} from "../../../../core/services/task.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import esLocale from "@fullcalendar/core/locales/es";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {UserDto} from "../../../user/models/user.dto";
import {TaskPriorityDto} from "../../models/task-priority.dto";

@Component({
    selector: 'app-task-calendar',
    templateUrl: './task-calendar.component.html',
    styleUrl: './task-calendar.component.scss',
    providers: [ConfirmationService, MessageService],
})
export class TaskCalendarComponent implements OnInit, AfterViewInit {

    @ViewChild('calendar') calendarComponent!: FullCalendarComponent;

    today: string = '';


    calendarOptions: any = {
        initialView: 'dayGridMonth',
    };

    dateFrom: string = '';
    dateTo: string = '';

    tasks: TaskDto[] = [];

    canAddTask: boolean = false;
    canEditTask: boolean = false;

    isLoading: boolean = false;

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

    selectedTask: TaskDto | null = null;
    showTaskDialog: boolean = false;

    menuItems: MenuItem[] = [];

    createSidebarVisible: boolean = false;

    editSidebarVisible: boolean = false;

    viewSidebarVisible: boolean = false;

    deadline: Date = new Date();

    taskId: number = 0;

    rendered: boolean = false;


    private searchSubject = new Subject<string>();

    constructor(
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private sharedService: SharedService,
        private activatedRoute: ActivatedRoute,
        private taskService: TaskService
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.permissions.includes('CREAR TAREAS')) {
                this.canAddTask = true;
                this.isModerator = true;
            }
            if (decoded.permissions.includes('EDITAR TAREAS')) {
                this.canEditTask = true;
                this.isModerator = true;
            }
            this.userId = decoded.userId;
        }
        this.keyword = this.sharedService.getData('keyword') ?? '';
        this.selectedStatus = this.sharedService.getData('selectedStatus') ?? [];
        this.selectedPriority = this.sharedService.getData('selectedPriority') ?? [];
    }


    ngOnInit() {
        // Initialize the today variable with the current date
        this.today = new Date().toISOString().split('T')[0];
        this.calendarOptions = {
            plugins: [
                dayGridPlugin,
                timeGridPlugin,
                interactionPlugin
            ],
            height: 720,
            initialDate: this.today,
            headerToolbar: {
                left: 'prev,next today', center: 'title', right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            editable: true,
            selectable: false,
            selectMirror: true,
            dayMaxEvents: true,
            eventDurationEditable: false,
            locale: esLocale,
            datesSet: (dateInfo: any) => this.onDatesSet(dateInfo),
            dateClick: (e: any) => this.onDateClick(e),
            eventClick: (e: any) => this.onEventClick(e),
            eventDrop: (e: any) => this.onEventDrop(e),
        };
        this.activatedRoute.parent?.params.subscribe(params => {
            this.calendarOptions = {...this.calendarOptions, ...{editable: ((this.isModerator) && this.canEditTask)}};
            this.getAllPriorities();
            this.getAllStatuses();
            this.searchSubject.pipe(debounceTime(500)).subscribe(() => {
                this.getData()
            });
        });
    }

    ngAfterViewInit() {
        this.rendered = true;
        this.getAllStatuses();
    }

    public onDatesSet(dateInfo: any) {
        if (this.rendered) {
            this.dateFrom = dateInfo.startStr;
            this.dateTo = dateInfo.endStr;
            this.getData()
        }
    }

    public onEventClick(e: any) {
        this.selectedTask = this.tasks.find(task => task.taskId === Number(e.event.id)) ?? null;
        this.showTaskDialog = true;
        this.generateMenu();
    }

    public onDateClick(e: any) {
        if (this.canAddTask && (this.isModerator)) {
            let currentDate = new Date();
            currentDate.setHours(0,
                0,
                0,
                0);
            let selectedDate = new Date(e.date);
            selectedDate.setHours(20,
                0,
                0,
                0);
            if (selectedDate > currentDate) {
                this.deadline = selectedDate;
                this.navigateToCreateTask();
            }
        }
    }


    public onEventDrop(e: any) {
        this.selectedTask = this.tasks.find(task => task.taskId === Number(e.event.id)) ?? null;
        if (new Date(e.event.start).getTime() < new Date().getTime()) {
            this.messageService.add({
                severity: 'error', summary: 'Error', detail: 'No puedes establecer una fecha límite en el pasado'
            });
            e.revert();
            return;
        } else if (this.selectedTask != null && this.selectedTask.taskEndDate) {
            this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'No puedes modificar la fecha límite de una tarea finalizada'
            });
            e.revert();
            return;
        }
        this.updateTaskDeadline(e.event.id,
            e.event.start);
    }

    public generateMenu() {
        if (this.selectedTask != null) {
            this.menuItems = [
                {
                    label: 'Ver tarea', command: () => this.navigateToViewTask(this.selectedTask!.taskId)
                },
                {
                    label: 'Editar tarea', command: () => this.navigateToEditTask(this.selectedTask!.taskId)
                },
                {label: 'Eliminar tarea', command: () => this.onDeleteTask(this.selectedTask!.taskId)}
            ];

            if (!((this.isModerator) && this.canEditTask && !this.selectedTask!.taskEndDate)) {
                this.menuItems = this.menuItems.filter(item => item.label === 'Ver tarea');
            }
        }
    }

    public navigateToCreateTask() {
        this.createSidebarVisible = true
    }

    public navigateToViewTask(taskId: number) {
        this.viewSidebarVisible = true
        this.taskId = taskId
    }

    public navigateToEditTask(taskId: number) {
        this.editSidebarVisible = true
        this.taskId = taskId
    }

    public onSearch(event: any) {
        this.keyword = event.target.value;
        this.sharedService.changeData('keyword',
            this.keyword);
        this.searchSubject.next(this.keyword);
    }


    public getData() {
        this.isLoading = true;
        this.taskService.getTasks('taskDueDate',
            'asc',
            0,
            100,
            this.keyword,
            this.selectedStatus.map(status => status.label),
            [],
            this.dateFrom.toString(),
            this.dateTo.toString(),).subscribe({
            next: (data: ResponseDto<PageDto<TaskDto>>) => {
                this.tasks = data.data!.content;
                this.tasks.forEach(task => {
                    task.taskAssignees.forEach((assignee: UserDto) => this.fetchUserImage(assignee.userId));
                });
                const events = this.tasks.map(task => {
                    const isTaskOverdue = this.checkIfTaskIsOverdue(task.taskStatus.taskStatusId,
                        task.taskDueDate);
                    return {
                        id: task.taskId.toString(),
                        title: (isTaskOverdue ? '⚠️' : '') + task.taskName,
                        start: task.taskDueDate,
                        end: task.taskDueDate,
                        backgroundColor: this.getPriorityColor(task.taskPriority.taskPriorityId),
                        borderColor: this.getPriorityColor(task.taskPriority.taskPriorityId),
                    }
                });
                let calendarApi = this.calendarComponent.getApi();
                calendarApi.removeAllEvents();
                calendarApi.addEventSource(events);
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
        this.showTaskDialog = false;
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
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
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

    public updateTaskDeadline(
        taskId: number,
        newTaskDeadline: Date
    ) {
        const task = this.tasks.find(task => task.taskId === Number(taskId));
        const taskAssigneeIds = task!.taskAssignees.map(assignee => assignee.userId);
        const taskFileIds = task!.taskFiles.map(file => file.fileId);
        this.taskService.updateTask(task!.taskId,
            task!.projectId,
            task!.taskName,
            task!.taskDescription,
            newTaskDeadline.toISOString(),
            task!.taskPriority.taskPriorityId,
            taskAssigneeIds,
            taskFileIds).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Fecha actualizada correctamente'
                });
            }, error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }
}

