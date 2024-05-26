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
      // Display notification if app is in the foreground
      // if (payload.notification) {
      //   new Notification(payload.notification.title!, {
      //     body: payload.notification.body,
      //     icon: payload.notification.image,
      //   });
      // }
      if (payload.data) {
        new Notification(payload.data['title']!, {
          body: payload.data['body'],
          icon: payload.data['image'],
        });
      }
    });
  }

  public getMessageObservable() {
    return this.messageSubject.asObservable();
  }
}
