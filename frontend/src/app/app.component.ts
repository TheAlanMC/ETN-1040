import {Component, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {PrimeNGConfig} from "primeng/api";
import {FirebaseService} from "./core/services/firebase.service";
import {Capacitor} from "@capacitor/core";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent  implements OnInit {
  title = 'frontend';
  private platform = Capacitor.getPlatform();

  constructor(private config: PrimeNGConfig, private translateService: TranslateService, private firebaseService: FirebaseService) {
    this.translateService.setDefaultLang('es');
    this.translateService.use('es');
    this.translateService.get('primeng').subscribe(res => {
      this.config.setTranslation(res)
    });
  }

    ngOnInit() {
      if (this.platform === 'ios' || this.platform === 'android') {
        this.firebaseService.listenToMessagesNative();
      } else {
        this.firebaseService.listenToMessagesWeb();
      }
    }
}
