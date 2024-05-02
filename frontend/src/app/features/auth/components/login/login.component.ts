import {Component} from '@angular/core';
import {AuthService} from "../../../../core/services/auth.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {LayoutService} from "../../../../layout/service/app.layout.service";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class LoginComponent {
  emailControl = new FormControl('', [Validators.required, Validators.email]);
  passwordControl = new FormControl('', [Validators.required]);
  // rememberMe: boolean = false;

  constructor(private layoutService: LayoutService, private authService: AuthService, private router: Router, private messageService: MessageService) {
    if (localStorage.getItem('token')) {
      this.router.navigate(['/home']).then(r => console.log('Already logged in'));
    }
  }

  login() {
    this.authService.login(this.emailControl.value!!, this.passwordControl.value!!).subscribe({
      next: (data) => {
        // Save token
        localStorage.setItem('token', data.data!!.token);
        this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Inicio de sesión exitoso'});
        setTimeout(() => {
          this.router.navigate(['/home']).then(r => console.log('Navigated to home'));
        }, 1000);
      },
      error: (error) => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
        console.log(error);
      }
    });
  }

  get dark(): boolean {
    return this.layoutService.config().colorScheme !== 'light';
  }
}

