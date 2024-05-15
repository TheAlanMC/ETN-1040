import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {TaskListDto} from "../../../task/models/task-list.dto";
import {MenuItem, MessageService} from "primeng/api";
import {ProjectTaskDeadlineComponent} from "../project-task-deadline/project-task-deadline.component";
import { UtilService } from '../../../../core/services/util.service';
import {TaskDto} from "../../../task/models/task.dto";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import {UserDto} from "../../../user/models/user.dto";
import {Router} from "@angular/router";
import {TaskService} from "../../../../core/services/task.service";
import {ResponseDto} from "../../../../core/models/response.dto";

@Component({
  selector: 'app-project-task-deadline-list',
  templateUrl: './project-task-deadline-list.component.html',
  styleUrl: './project-task-deadline-list.component.scss',
  providers: [MessageService]
})
export class ProjectTaskDeadlineListComponent implements OnInit {

  @Input() taskList!: TaskListDto;

  @Input() taskListIds!: string[];

  @Input() users!: UserDto[];

  @Input() isOwner: boolean = false;

  @Input() isModerator: boolean = false;

  @Input() isMember: boolean = false;

  title: string = '';

  timeout: any = null;

  isMobileDevice: boolean = false;

  @ViewChild('inputEl') inputEl!: ElementRef;

  @ViewChild('listEl') listEl!: ElementRef;

  constructor(public parent: ProjectTaskDeadlineComponent, private utilService: UtilService, private router: Router, private taskService: TaskService, private messageService: MessageService) {
    this.isMobileDevice = this.utilService.checkIfMobile();
  }

  ngOnInit(): void {
  }

  onCardClick(event: Event, card: TaskDto) {
    const eventTarget = event.target as HTMLElement;
    if (!(eventTarget.classList.contains('p-button-icon') || eventTarget.classList.contains('p-trigger'))) {
      if (this.taskList.listId) {
        this.navigateToViewTask(card.taskId);
      }
    }
  }

  dropCard(event: CdkDragDrop<TaskDto[]>): void {
    const itemBeingMoved = event.previousContainer.data[event.previousIndex];
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
      this.updateTaskDeadline(itemBeingMoved, event.container.id);
    }

    // Sort the tasks in the source list
    event.previousContainer.data.sort((a, b) => new Date(a.taskDeadline).getTime() - new Date(b.taskDeadline).getTime());

    // If the item was moved to a different list, sort the tasks in the destination list
    if (event.previousContainer !== event.container) {
      event.container.data.sort((a, b) => new Date(a.taskDeadline).getTime() - new Date(b.taskDeadline).getTime());
    }
  }

  public updateTaskDeadline(task: TaskDto, listId: string) {
    // Determine the new deadline based on the listId, if 2 set to today, if 3 set to tomorrow, if 4 set to next week, if 5 set two weeks from now
    let newTaskDeadline = new Date();
    switch (listId) {
      case '2':
        newTaskDeadline.setDate(newTaskDeadline.getDate());
        break;
      case '3':
        newTaskDeadline.setDate(newTaskDeadline.getDate() + 1);
        break;
      case '4':
        newTaskDeadline.setDate(newTaskDeadline.getDate() + 7);
        break;
      case '5':
        newTaskDeadline.setDate(newTaskDeadline.getDate() + 14);
        break;
    }
    newTaskDeadline.setHours(23, 59, 59, 999);
    console.log(newTaskDeadline);
      this.taskService.updateTask(task.taskId, task.taskName, task.taskDescription, newTaskDeadline.toISOString(), task.taskPriority, task.taskAssigneeIds, task.taskFileIds).subscribe({
        next: (data: ResponseDto<null>) => {
          this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Fecha límite de la tarea actualizada con éxito'});
          this.taskList.tasks.find(t => t.taskId === task.taskId)!.taskDeadline = newTaskDeadline;
        },
        error: (err) => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'No se pudo actualizar la fecha límite de la tarea'});
        }
      });
      }

  getConnectedListIds(): string[] {
    const restrictedListId = '1';
    return this.taskListIds.filter(id => id !== restrictedListId);
  }

  public navigateToViewTask(taskId: number) {
    this.router.navigate(['/tasks/view/' + taskId]).then(r => console.log('Navigate to view task'));
  }

  focus() {
    this.timeout = setTimeout(() => this.inputEl.nativeElement.focus(), 1);
  }

  insertHeight(event: any) {
    event.container.element.nativeElement.style.minHeight = '10rem';
  }

  removeHeight(event: any) {
    event.container.element.nativeElement.style.minHeight = '2rem';
  }

}
