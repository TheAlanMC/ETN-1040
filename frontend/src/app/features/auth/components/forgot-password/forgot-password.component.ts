import {Component} from '@angular/core';
import {LayoutService} from "../../../../layout/service/app.layout.service";
import {ConfirmationService, MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {AuthService} from "../../../../core/services/auth.service";
import {FormControl, Validators} from "@angular/forms";
import {SharedService} from "../../../../core/services/shared.service";

@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrl: './forgot-password.component.scss',
    providers: [
        MessageService,
        ConfirmationService
    ],
})
export class ForgotPasswordComponent {
    isLoading: boolean = false;

    emailControl = new FormControl('',
        [
            Validators.required,
            Validators.email
        ]);

    constructor(
        private layoutService: LayoutService,
        private authService: AuthService,
        private router: Router,
        private messageService: MessageService,
        private sharedService: SharedService
    ) {
    }

    get dark(): boolean {
        return this.layoutService.config().colorScheme !== 'light';
    }

    onSubmit() {
        this.isLoading = true;
        this.authService.forgotPassword(this.emailControl.value!).subscribe({
            next: (data) => {
                this.sharedService.changeData('email',
                    this.emailControl.value);
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Correo enviado'});
                setTimeout(() => {
                        this.router.navigate(['/auth/verification']).then(r => console.log('Navigated to home'));
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
}
