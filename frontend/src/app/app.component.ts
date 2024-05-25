import {Component, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {PrimeNGConfig} from "primeng/api";
import {getMessaging, getToken, onMessage} from "firebase/messaging";
import {environment} from "../environments/environment";
import {FirebaseService} from "./core/services/firebase.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent  implements OnInit {
  title = 'frontend';

  constructor(private config: PrimeNGConfig, private translateService: TranslateService, private firebaseService: FirebaseService) {
    this.translateService.setDefaultLang('es');
    this.translateService.use('es');
    this.translateService.get('primeng').subscribe(res => {
      this.config.setTranslation(res)
    });
  }

    ngOnInit() {
        this.firebaseService.listenToMessages();
    }

   public requestPermission() {
    const messaging = getMessaging();
    getToken(messaging, {vapidKey: environment.firebase.vapidKey}).then((currentToken) => {
      if (currentToken) {
        console.log('Token:', currentToken);
      } else {
        console.log('No registration token available. Request permission to generate one.');
      }
    }).catch((err) => {
      console.log('An error occurred while retrieving token. ', err);
    });
   }

    public listenToMessages() {
        const messaging = getMessaging();
        onMessage(messaging, (payload) => {
            console.log('Message received. ', payload);
            // Handle the message here
        });
    }
}
