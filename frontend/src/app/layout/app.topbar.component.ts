import {Component, ElementRef, ViewChild} from '@angular/core';
import {LayoutService} from './service/app.layout.service';
import {UserService} from "../core/services/user.service";

@Component({
    selector: 'app-topbar',
    templateUrl: './app.topbar.component.html'
})
export class AppTopbarComponent {
  profilePictureUrl: string = 'assets/layout/images/avatar.png';

  @ViewChild('menubutton') menuButton!: ElementRef;

    constructor(public layoutService: LayoutService, private userService: UserService) {
      this.getProfilePictureUrl();
    }

    onMenuButtonClick() {
        this.layoutService.onMenuToggle();
    }

    onProfileButtonClick() {
        this.layoutService.showProfileSidebar();
    }
    onConfigButtonClick() {
        this.layoutService.showConfigSidebar();
    }

  public getProfilePictureUrl(){
      this.userService.getProfilePicture().subscribe({
        next: (data) => {
          this.profilePictureUrl = URL.createObjectURL(data);
        },
        error: (error) => {
          console.log(error);
        }
      });
  }

}
