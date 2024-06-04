import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../../core/services/auth.service";
import {Router} from "@angular/router";
import {FormControl, Validators} from "@angular/forms";
import {LayoutService} from "../../../../layout/service/app.layout.service";
import {ConfirmationService, MessageService} from "primeng/api";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {FirebaseService} from "../../../../core/services/firebase.service";
import {UtilService} from "../../../../core/services/util.service";

@Component({
    selector: 'app-login', templateUrl: './login.component.html', styleUrl: './login.component.scss', providers: [
        MessageService,
        ConfirmationService,
    ],
})
export class LoginComponent implements OnInit {
    isLoading: boolean = false;
    isChromium: boolean = false;
    isMobile: boolean = false;
    token: string = '';
    emailControl = new FormControl('',
        [
            Validators.required,
            Validators.email,
        ]);
    passwordControl = new FormControl('',
        [Validators.required]);

    // rememberMe: boolean = false;

    constructor(
        private layoutService: LayoutService,
        private authService: AuthService,
        private router: Router,
        private messageService: MessageService,
        private firebaseService: FirebaseService,
        private utilService: UtilService,
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            // Check if token is not expired
            if (decoded.exp > Date.now() / 1000) {
                this.router.navigate(['/']).then(r => console.log('Already logged in'));
            }
        }
        this.isChromium = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
    }

    ngOnInit(): void {
        this.isMobile = this.utilService.checkIfMobile();
    }

    get dark(): boolean {
        return this.layoutService.config().colorScheme !== 'light';
    }

    public onLogin() {
        if (this.isChromium && !this.isMobile) {
            this.login();
        } else {
            this.isLoading = true;
            this.firebaseService.getFirebaseToken().subscribe({
                next: (token) => {
                    this.token = token;
                    this.login();
                },
                error: (error) => {
                    console.error(error);
                    this.isLoading = false;
                    this.messageService.add({severity: 'error', summary: 'Error', detail: 'Por favor, permita las notificaciones para iniciar sesión'});
                },
            });
        }
    }


    public login() {
        this.isLoading = true;
        this.authService.login(this.emailControl.value!,
            this.passwordControl.value!,
            this.token).subscribe({
            next: (data) => {
                // Save token
                localStorage.setItem('token',
                    data.data!.token);
                localStorage.setItem('refreshToken',
                    data.data!.refreshToken);
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Inicio de sesión exitoso'});
                setTimeout(() => {
                        this.router.navigate(['/']).then(r => console.log('Navigated to home'));
                        this.isLoading = false;
                    },
                    500);
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onEnter(
        event: Event,
        loginButton: HTMLButtonElement
    ) {
        loginButton.click();
    }
}

