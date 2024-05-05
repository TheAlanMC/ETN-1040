import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private dataMapSource = new BehaviorSubject<Map<string, any>>(new Map());

  constructor() { }

  changeData(key: string, value: any) {
    let currentMap = this.dataMapSource.value;
    currentMap.set(key, value);
    this.dataMapSource.next(currentMap);
  }

  getData(key: string) {
    let currentMap = this.dataMapSource.value;
    return currentMap.get(key);
  }
}
