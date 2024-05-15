import {Component, Input, OnInit} from '@angular/core';
import {TaskDto} from "../../../task/models/task.dto";
import {MenuItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {UtilService} from "../../../../core/services/util.service";

@Component({
  selector: 'app-project-task-deadline-card',
  templateUrl: './project-task-deadline-card.component.html',
  styleUrl: './project-task-deadline-card.component.scss'
})
export class ProjectTaskDeadlineCardComponent implements OnInit{

  @Input() card!: TaskDto;

  @Input() listId!: string;

  @Input() users!: UserDto[];

  taskLists: any[] = [
    {
      listId: '1',
      title: 'Vencido',
    },
    {
      listId: '2',
      title: 'Para hoy',
    },
    {
      listId: '3',
      title: 'Para esta semana',
    },
    {
      listId: '4',
      title: 'Para la próxima semana',
    },
    {
      listId: '5',
      title: 'Para más de una semana',
    },
  ];

  menuItems: MenuItem[] = [];

  imgLoaded: { [key: string]: boolean } = {};

  baseUrl: string = `${environment.API_URL}/api/v1/users`;



  constructor(private utilService: UtilService) {
    let subMenu = this.taskLists.map(d => ({id: d.listId, label: d.title, command: () => this.onMove(d.listId)}));
    this.generateMenu(subMenu);
  }

  ngOnInit() {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
  }

  onDelete() {
    // this.kanbanService.deleteCard(this.card.id, this.listId);
  }

  onCopy() {
    // this.kanbanService.copyCard(this.card, this.listId);
  }

  onMove(listId: string) {
    // this.kanbanService.moveCard(this.card, listId, this.listId);
  }


  generateMenu(subMenu: any[]) {
    this.menuItems = [
      {label: 'Copy card', command: () => this.onCopy()},
      {label: 'Move card', items: subMenu},
      {label: 'Delete card', command: () => this.onDelete()}
    ];
  }

  public getImageLoaded(userId: any): boolean {
    return this.imgLoaded[userId] ?? false;
  }

  public setImageLoaded(userId: any, value: boolean) {
    this.imgLoaded[userId] = value;
  }

  public getFullName(userId: any): string {
    const user = this.users.find(user => user.userId === userId);
    return `${user?.firstName} ${user?.lastName}`;
  }

  public getEmail(userId: any): string {
    const user = this.users.find(user => user.userId === userId);
    return user?.email ?? '';
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

  public checkIfTaskIsOverdue(statusId: number, taskDeadline: Date): boolean {
    if (statusId === 3) {
      return false;
    }
    return new Date(taskDeadline).getTime() < new Date().getTime();
  }

}
