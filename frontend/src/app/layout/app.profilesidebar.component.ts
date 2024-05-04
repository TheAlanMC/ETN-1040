import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';
import {Router} from "@angular/router";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../core/models/jwt-payload.dto";
import {ConfirmationService} from "primeng/api";

interface Notification {
  icon: string;
  message: string;
  time: Date
}
@Component({
    selector: 'app-profilemenu',
    templateUrl: './app.profilesidebar.component.html',
    providers: [ConfirmationService]
})
export class AppProfileSidebarComponent {

  decoded: JwtPayload | undefined

  notifications: Notification[] = [
    {icon: 'pi pi-envelope text-xl text-primary', message: 'Nuevo mensaje de contacto', time: new Date("2024-05-02 12:00:00")},
    {icon: 'pi pi-user-plus text-xl text-primary', message: 'Nueva solicitud de contacto', time: new Date('2024-05-01 12:00:00')},
    {icon: 'pi pi-comment text-xl text-primary', message: 'Nuevo comentario en una publicación', time: new Date('2024-04-30 12:00:00')},
  ]

  constructor(private layoutService: LayoutService, private confirmationService: ConfirmationService,private router: Router) {
    // Get token from local storage
    const token = localStorage.getItem('token');
    // Check if token exists
    if (token) {
      this.decoded = jwtDecode<JwtPayload>(token!!);
      // Check if token is not expired
      if (this.decoded.exp < Date.now() / 1000) {
        this.router.navigate(['/']).then(r => console.log('Redirect to login'))
      }
    }
  }

  get visible(): boolean {
    return this.layoutService.state.profileSidebarVisible;
  }

  set visible(_val: boolean) {
    this.layoutService.state.profileSidebarVisible = _val;
  }

  onProfile() {
    this.visible = false;
    this.router.navigate(['/users/profile']).then(r => console.log('Redirect to profile'))
  }

  onLogout() {
    this.confirmationService.confirm({
      key: 'confirmLogout',
      message: '¿Estás seguro de cerrar sesión?',
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => {
        this.visible = false;
        this.logout();
      },
      reject: () => {
        this.visible = false;
      }
    });
  }

  public logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'))
  }
}
