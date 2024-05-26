import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../../core/services/auth.service";
import {Router} from "@angular/router";
import {FormControl, Validators} from "@angular/forms";
import {LayoutService} from "../../../../layout/service/app.layout.service";
import {ConfirmationService, MessageService} from "primeng/api";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {FirebaseService} from "../../../../core/services/firebase.service";

@Component({
    selector: 'app-login', templateUrl: './login.component.html', styleUrl: './login.component.scss', providers: [
        MessageService,
        ConfirmationService,
    ],
})
export class LoginComponent implements OnInit {
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
    }

    ngOnInit(): void {
        this.firebaseService.getFirebaseToken().subscribe({
            next: (token) => {
                this.token = token;
            },
            error: (error) => {
                console.log(error);
            },
        });
    }

    get dark(): boolean {
        return this.layoutService.config().colorScheme !== 'light';
    }

    login() {
        this.authService.login(this.emailControl.value!,
            this.passwordControl.value!,this.token).subscribe({
            next: (data) => {
                // Save token
                localStorage.setItem('token',
                    data.data!.token);
                localStorage.setItem('refreshToken',
                    data.data!.refreshToken);
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Inicio de sesión exitoso'});
                setTimeout(() => {
                        this.router.navigate(['/']).then(r => console.log('Navigated to home'));
                    },
                    500);
            }, error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
                console.log(error);
            }
        });
    }

    onEnter(
        event: Event,
        loginButton: HTMLButtonElement
    ) {
        loginButton.click();
    }
}

