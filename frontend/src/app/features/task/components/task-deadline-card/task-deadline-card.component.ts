import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TaskDto} from "../../models/task.dto";
import {UserDto} from "../../../user/models/user.dto";
import {MenuItem} from "primeng/api";
import {environment} from "../../../../../environments/environment";
import {SharedService} from "../../../../core/services/shared.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {UtilService} from "../../../../core/services/util.service";

@Component({
    selector: 'app-task-deadline-card',
    templateUrl: './task-deadline-card.component.html',
    styleUrl: './task-deadline-card.component.scss',
})
export class TaskDeadlineCardComponent implements OnInit {

    @Input() card!: TaskDto;

    @Input() listId!: string;

    @Input() users!: UserDto[];

    @Output() moveCard = new EventEmitter<{ card: TaskDto, listId: string }>();

    @Output() deleteCard = new EventEmitter<TaskDto>();

    @Output() viewCard = new EventEmitter<TaskDto>();

    @Output() editCard = new EventEmitter<TaskDto>();

    isModerator: boolean = false;

    taskLists: any[] = [{
        listId: '2', title: 'Para hoy',
    }, {
        listId: '3', title: 'Para esta semana',
    }, {
        listId: '4', title: 'Para la próxima semana',
    }, {
        listId: '5', title: 'Para más de una semana',
    },];

    menuItems: MenuItem[] = [];

    imgLoaded: { [key: string]: boolean } = {};

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    canEditTask: boolean = false;

    constructor(
        private sharedService: SharedService,
        private utilService: UtilService
    ) {
    }

    ngOnInit() {
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.permissions.includes('EDITAR TAREAS')) {
                this.canEditTask = true;
            }
        }

        this.isModerator = this.sharedService.getData('isModerator')
        let subMenu = this.taskLists.map(d => ({id: d.listId, label: d.title, command: () => this.onMove(d.listId)}));
        this.generateMenu(subMenu);
    }

    public onDelete() {
        this.deleteCard.emit(this.card);
    }

    public onMove(listId: string) {
        if (listId === this.listId) {
            return;
        }
        this.moveCard.emit({card: this.card, listId: listId});
    }


    public generateMenu(subMenu: any[]) {
        this.menuItems = [{label: 'Ver tarea', command: () => this.viewCard.emit(this.card)}, {
            label: 'Editar tarea', command: () => this.editCard.emit(this.card)
        }, {label: 'Mover tarea', items: subMenu}, {label: 'Eliminar tarea', command: () => this.onDelete()}];

        if (!((this.isModerator) && this.canEditTask && !this.card.taskEndDate)) {
            this.menuItems = this.menuItems.filter(item => item.label === 'Ver tarea');
        }
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

}
