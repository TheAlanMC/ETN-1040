import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TaskListDto} from "../../../task/models/task-list.dto";
import {ConfirmationService, MessageService} from "primeng/api";
import {ProjectTaskDeadlineComponent} from "../project-task-deadline/project-task-deadline.component";
import {UtilService} from '../../../../core/services/util.service';
import {TaskDto} from "../../../task/models/task.dto";
import {CdkDragDrop, moveItemInArray, transferArrayItem} from '@angular/cdk/drag-drop';
import {UserDto} from "../../../user/models/user.dto";
import {Router} from "@angular/router";
import {TaskService} from "../../../../core/services/task.service";
import {SharedService} from "../../../../core/services/shared.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
    selector: 'app-project-task-deadline-list',
    templateUrl: './project-task-deadline-list.component.html',
    styleUrl: './project-task-deadline-list.component.scss',
    providers: [MessageService, ConfirmationService],
})
export class ProjectTaskDeadlineListComponent implements OnInit {

    @Input() taskList!: TaskListDto;

    @Input() taskListIds!: string[];

    @Input() users!: UserDto[];

    @Output() viewCard = new EventEmitter<TaskDto>();

    @Output() editCard = new EventEmitter<TaskDto>();

    title: string = '';

    isMobileDevice: boolean = false;

    @ViewChild('inputEl') inputEl!: ElementRef;

    @ViewChild('listEl') listEl!: ElementRef;

    canEditTask: boolean = false;

    isOwner: boolean = false;
    isModerator: boolean = false;
    projectId: number = 0;

    constructor(
        public parent: ProjectTaskDeadlineComponent,
        private utilService: UtilService,
        private router: Router,
        private taskService: TaskService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private sharedService: SharedService
    ) {
        this.isMobileDevice = this.utilService.checkIfMobile();
    }

    ngOnInit() {
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.roles.includes('EDITAR TAREAS')) {
                this.canEditTask = true;
            }
        }
        this.isOwner = this.sharedService.getData('isOwner')
        this.isModerator = this.sharedService.getData('isModerator')
        this.projectId = this.sharedService.getData('projectId')
    }

    public onCardClick(
        event: Event,
        card: TaskDto
    ) {
        const eventTarget = event.target as HTMLElement;
        if (!(eventTarget.classList.contains('p-button-icon') || eventTarget.classList.contains('p-trigger') || eventTarget.classList.contains('p-avatar-text') || eventTarget.classList.contains('ng-star-inserted'))) {
            if (this.taskList.listId) {
                this.viewCard.emit(card);
            }
        }
    }

    public dropCard(event: CdkDragDrop<TaskDto[]>) {
        const itemBeingMoved = event.previousContainer.data[event.previousIndex];
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data,
                event.previousIndex,
                event.currentIndex);
        } else {
            transferArrayItem(event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex);
            this.updateTaskDeadline(itemBeingMoved,
                event.container.id);
        }
        // Sort the tasks in the source list
        event.previousContainer.data.sort((
            a,
            b
        ) => new Date(a.taskDueDate).getTime() - new Date(b.taskDueDate).getTime());

        // If the item was moved to a different list, sort the tasks in the destination list
        if (event.previousContainer !== event.container) {
            event.container.data.sort((
                a,
                b
            ) => new Date(a.taskDueDate).getTime() - new Date(b.taskDueDate).getTime());
        }
    }

    public updateTaskDeadline(
        task: TaskDto,
        listId: string,
        refresh: boolean = false
    ) {
        // Determine the new deadline based on the listId, if 2 set to today, if 3 set to tomorrow, if 4 set to next week, if 5 set two weeks from now
        let newTaskDeadline = new Date();
        switch (listId) {
            case '2':
                if (newTaskDeadline.getHours() >= 20) {
                    newTaskDeadline.setDate(newTaskDeadline.getDate() + 1);
                } else {
                    newTaskDeadline.setDate(newTaskDeadline.getDate());
                }
                break;
            case '3':
                newTaskDeadline.setDate(newTaskDeadline.getDate() + 2);
                break;
            case '4':
                newTaskDeadline.setDate(newTaskDeadline.getDate() + 7);
                break;
            case '5':
                newTaskDeadline.setDate(newTaskDeadline.getDate() + 14);
                break;
        }
        newTaskDeadline.setHours(20,
            0,
            0,
            0);
        const taskAssigneeIds = task.taskAssignees.map(assignee => assignee.userId);
        const taskFileIds = task.taskFiles.map(file => file.fileId);
        this.taskService.updateTask(task.taskId,
            this.projectId,
            task.taskName,
            task.taskDescription,
            newTaskDeadline.toISOString(),
            task.taskPriority.taskPriorityId,
            taskAssigneeIds,
            taskFileIds).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Fecha límite de la tarea actualizada con éxito'
                });
                if (refresh) {
                    setTimeout(() => {
                            this.router.navigateByUrl('/',
                                {skipLocationChange: true}).then(() => {
                                this.router.navigate(['/projects/view/' + this.projectId + '/task-deadline',
                                    {dummy: Date.now()}]).then(r => console.log('Task deadline updated successfully'));
                            });
                        },
                        500);
                } else {
                    this.taskList.tasks.find(t => t.taskId === task.taskId)!.taskDueDate = newTaskDeadline;
                }
            }, error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public getConnectedListIds(): string[] {
        const restrictedListId = '1';
        return this.taskListIds.filter(id => id !== restrictedListId);
    }

    public insertHeight(event: any) {
        event.container.element.nativeElement.style.minHeight = '10rem';
    }

    public removeHeight(event: any) {
        event.container.element.nativeElement.style.minHeight = '2rem';
    }

    public onMoveCard(event: any) {
        this.updateTaskDeadline(event.card,
            event.listId,
            true);
    }

    public onDeleteCard(event: any) {
        this.onDeleteTask(event.taskId,
            true);
    }

    public onEditCard(event: any) {
        this.editCard.emit(event);
    }

    public onViewCard(event: any) {
        this.viewCard.emit(event);
    }

    public onDeleteTask(
        taskId: number,
        refresh: boolean = false
    ) {
        this.confirmationService.confirm({
            key: 'confirmDeleteTask',
            message: '¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.deleteTask(taskId,
                    refresh);
            },
        });
    }

    public deleteTask(
        taskId: number,
        refresh: boolean = false
    ) {
        this.taskService.deleteTask(taskId).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Tarea eliminada correctamente'
                });
                if (refresh) {
                    // Wait 500ms before refreshing the page
                    setTimeout(() => {
                            this.router.navigateByUrl('/',
                                {skipLocationChange: true}).then(() => {
                                this.router.navigate(['/projects/view/' + this.projectId + '/task-deadline',
                                    {dummy: Date.now()}]).then(r => console.log('Task deleted successfully'));
                            });
                        },
                        500);
                } else {
                    this.taskList.tasks = this.taskList.tasks.filter(task => task.taskId !== taskId);
                }
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

}
