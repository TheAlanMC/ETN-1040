import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TaskDto} from "../../../task/models/task.dto";
import {MenuItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {UtilService} from "../../../../core/services/util.service";
import {Router} from "@angular/router";
import {SharedService} from "../../../../core/services/shared.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
    selector: 'app-project-task-deadline-card',
    templateUrl: './project-task-deadline-card.component.html',
    styleUrl: './project-task-deadline-card.component.scss',
})
export class ProjectTaskDeadlineCardComponent implements OnInit {

    @Input() card!: TaskDto;

    @Input() listId!: string;

    @Input() users!: UserDto[];

    @Output() moveCard = new EventEmitter<{ card: TaskDto, listId: string }>();

    @Output() deleteCard = new EventEmitter<TaskDto>();

    @Output() viewCard = new EventEmitter<TaskDto>();

    @Output() editCard = new EventEmitter<TaskDto>();

    isOwner: boolean = false;
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
        private utilService: UtilService,
        private router: Router,
        private sharedService: SharedService
    ) {
    }

    ngOnInit() {
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.roles.includes('EDITAR TAREAS')) {
                this.canEditTask = true;
            }
        }
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        this.isOwner = this.sharedService.getData('isOwner')
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

        if (!((this.isOwner || this.isModerator) && this.canEditTask)) {
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
        taskDeadline: Date
    ): boolean {
        if (statusId === 3) {
            return false;
        }
        return new Date(taskDeadline).getTime() < new Date().getTime();
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

}
