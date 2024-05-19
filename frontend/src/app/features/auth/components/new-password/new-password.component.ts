import {Component, OnInit} from '@angular/core';
import {LayoutService} from "../../../../layout/service/app.layout.service";
import {ConfirmationService, MessageService} from "primeng/api";
import {AuthService} from "../../../../core/services/auth.service";
import {Router} from "@angular/router";
import {SharedService} from "../../../../core/services/shared.service";
import {FormControl, Validators} from "@angular/forms";

@Component({
    selector: 'app-new-password', templateUrl: './new-password.component.html', styleUrl: './new-password.component.scss', providers: [
        MessageService,
        ConfirmationService
    ]
})
export class NewPasswordComponent implements OnInit {
    passwordControl = new FormControl('', [
        Validators.required,
        Validators.minLength(8)
    ]);
    confirmPasswordControl = new FormControl('', [
        Validators.required,
        Validators.minLength(8)
    ]);

    constructor(private layoutService: LayoutService, private authService: AuthService, private router: Router, private messageService: MessageService, private sharedService: SharedService) {
    }

    get dark(): boolean {
        return this.layoutService.config().colorScheme !== 'light';
    }

    ngOnInit() {
        if (this.sharedService.getData('email') == null || this.sharedService.getData('code') == null) {
            console.log('No email or code found');
            this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
        }
    }

    onSubmit() {
        this.authService.resetPassword(this.sharedService.getData('email'), this.sharedService.getData('code'), this.passwordControl.value!, this.confirmPasswordControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Contraseña restablecida'});
                setTimeout(() => {
                    this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
                }, 500);
            }, error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
                console.log(error);
            }
        });
    }
}
