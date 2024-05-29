import {Injectable} from '@angular/core';
import {getMessaging, getToken, onMessage} from "firebase/messaging";
import {environment} from "../../../environments/environment";
import {from, Subject} from "rxjs";
import {FCM} from '@capacitor-community/fcm';
import {Capacitor} from "@capacitor/core";
import {PushNotifications} from '@capacitor/push-notifications';


@Injectable({
    providedIn: 'root'
})
export class FirebaseService {
    public platform = Capacitor.getPlatform();
    public messageSubject = new Subject<any>();

    constructor() {
    }

    public getFirebaseToken() {
        if (this.platform === 'ios' || this.platform === 'android') {
            return from(this.getFirebaseTokenNative());
        } else {
            return from(this.getFirebaseTokenWeb());
        }
    }

    public async getFirebaseTokenWeb() {
        const messaging = getMessaging();
        return getToken(messaging,
            {vapidKey: environment.firebase.vapidKey});
    }

    public async getFirebaseTokenNative() {
        const {token} = await FCM.getToken();
        return token;
    }

    public listenToMessagesWeb() {
        const messaging = getMessaging();
        onMessage(messaging,
            (payload) => {
                this.messageSubject.next(payload);
                if (payload.data) {
                    new Notification(payload.data['title']!,
                        {
                            body: payload.data['body'],
                            icon: payload.data['image'],
                        });
                }
            });
    }

    public listenToMessagesNative() {
        PushNotifications.addListener('pushNotificationReceived',
            (notification) => {
                this.messageSubject.next(notification);
            });
    }

    public getMessageObservable() {
        return this.messageSubject.asObservable();
    }
}

