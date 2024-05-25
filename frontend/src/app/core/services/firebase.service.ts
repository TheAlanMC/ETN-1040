import { Injectable } from '@angular/core';
import {getMessaging, getToken, onMessage} from "firebase/messaging";
import {environment} from "../../../environments/environment";
import {from, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FirebaseService {
  private messageSubject = new Subject<any>();

  constructor() { }


  public getFirebaseToken() {
    const messaging = getMessaging();
    return from(getToken(messaging, {vapidKey: environment.firebase.vapidKey}));
  }

  public listenToMessages() {
    const messaging = getMessaging();
    onMessage(messaging, (payload) => {
      this.messageSubject.next(payload);
    });
  }

  public getMessageObservable() {
    return this.messageSubject.asObservable();
  }
}
