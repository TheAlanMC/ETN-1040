// import { HttpClient } from '@angular/common/http';
// import { Injectable } from '@angular/core';
// import { BehaviorSubject, Subject } from 'rxjs';
// import {TaskListDto} from "../../features/task/models/task-list.dto";
// import {TaskDto} from "../../features/task/models/task.dto";
//
// @Injectable()
// export class TaskListService {
//
//   private _lists: TaskListDto[] = [];
//
//   private selectedCard = new Subject<TaskDto>();
//
//   private selectedListId = new Subject<string>();
//
//   private lists = new BehaviorSubject<TaskListDto[]>(this._lists);
//
//   private listNames = new Subject<any[]>();
//
//   lists$ = this.lists.asObservable();
//
//   selectedCard$ = this.selectedCard.asObservable();
//
//   selectedListId$ = this.selectedListId.asObservable();
//
//   listNames$ = this.listNames.asObservable();
//
//   constructor(private http: HttpClient) {
//   }
//
//   private updateLists(data: any[]) {
//     this._lists = data;
//     let small = data.map(l => ({listId: l.listId, title: l.title}));
//     this.listNames.next(small)
//     this.lists.next(data);
//   }
//
//   addList() {
//     const listId = this.generateId();
//     const title = "Untitled List";
//     const newList = {
//       listId: listId,
//       title: title,
//       tasks: []
//     };
//     this._lists.push(newList);
//     this.lists.next(this._lists);
//   }
//
//   deleteCard(cardId: string, listId: string) {
//     let lists = [];
//
//     for (let i = 0; i < this._lists.length; i++) {
//       let list = this._lists[i];
//
//       if (list.listId === listId && list.cards) {
//         list.cards = list.cards.filter(c => c.id !== cardId);
//       }
//
//       lists.push(list);
//     }
//
//     this.updateLists(lists);
//   }
//
//   moveCard(card: KanbanCard, targetListId: string, sourceListId: string) {
//     if (card.id) {
//       this.deleteCard(card.id, sourceListId);
//       let lists = this._lists.map(l => l.listId === targetListId ? ({...l, cards: [...l.cards || [], card]}) : l);
//       this.updateLists(lists);
//     }
//   }
//
//   onCardSelect(card: KanbanCard, listId: string) {
//     this.selectedCard.next(card);
//     this.selectedListId.next(listId);
//   }
//
//   generateId() {
//     let text = "";
//     let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//
//     for (var i = 0; i < 5; i++) {
//       text += possible.charAt(Math.floor(Math.random() * possible.length));
//     }
//
//     return text;
//   }
//
//   isMobileDevice() {
//     return (/iPad|iPhone|iPod/.test(navigator.userAgent)) || (/(android)/i.test(navigator.userAgent));
//   }
//
// }
