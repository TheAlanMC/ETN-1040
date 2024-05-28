import {Component, OnDestroy, OnInit} from '@angular/core';
import {LayoutService} from './service/app.layout.service';
import {Router} from "@angular/router";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../core/models/jwt-payload.dto";
import {ConfirmationService} from "primeng/api";
import {NotificationService} from "../core/services/notification.service";
import {NotificationDto} from "../core/models/notification.dto";
import {FirebaseService} from "../core/services/firebase.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-profilemenu',
    templateUrl: './app.profilesidebar.component.html',
    providers: [ConfirmationService]
})
export class AppProfileSidebarComponent implements OnInit, OnDestroy {

    decoded: JwtPayload | undefined

    notifications: NotificationDto[] = []

    messageSubscription: Subscription;


    // notifications: Notification[] = [
    //   {icon: 'pi pi-envelope text-xl text-primary', message: 'Nuevo mensaje de contacto', time: new Date("2024-05-02 12:00:00")},
    //   {icon: 'pi pi-user-plus text-xl text-primary', message: 'Nueva solicitud de contacto', time: new Date('2024-05-01 12:00:00')},
    //   {icon: 'pi pi-comment text-xl text-primary', message: 'Nuevo comentario en una publicación', time: new Date('2024-04-30 12:00:00')},
    // ]

    constructor(
        private layoutService: LayoutService,
        private confirmationService: ConfirmationService,
        private router: Router,
        private notificationService: NotificationService,
        private firebaseService: FirebaseService
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            this.decoded = jwtDecode<JwtPayload>(token!);
        }
        this.messageSubscription = new Subscription(); // Initialize here
    }

    get visible(): boolean {
        return this.layoutService.state.profileSidebarVisible;
    }

    set visible(_val: boolean) {
        this.layoutService.state.profileSidebarVisible = _val;
    }

    ngOnInit() {
        this.getAllNotifications();
        this.messageSubscription = this.firebaseService.getMessageObservable().subscribe({
            next: (data) => {
                this.getAllNotifications();
            },
            error: (error) => {
                console.error(error);
            }
        });
    }

    ngOnDestroy() {
        if (this.messageSubscription) {
            this.messageSubscription.unsubscribe();
        }
    }

    public getAllNotifications() {
        this.notificationService.getNotifications().subscribe({
            next: (data) => {
                this.notifications = data.data!;
                this.notifications.forEach(notification => {
                    switch (notification.messageTitle.split(' ')[0]) {
                        case 'Tarea':
                            notification.icon = 'pi pi-check-square text-xl text-primary';
                            break;
                        case 'Proyecto':
                            notification.icon = 'pi pi-briefcase text-xl text-primary';
                            break;
                        case 'Recordatorio:':
                            notification.icon = 'pi pi-bell text-xl text-primary';
                            break;
                        default:
                            notification.icon = 'pi pi-bell text-xl text-primary';
                            break;
                    }
                });
            },
            error: (error) => {
                console.error(error);
            }
        });
    }

    public markAsRead(notificationId: number) {
        this.notificationService.markAsRead(notificationId).subscribe({
            next: (data) => {
                this.getAllNotifications();
            },
            error: (error) => {
                console.error(error);
            }
        });
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

    public logout() {
        localStorage.removeItem('token');
        this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'))
    }

}
