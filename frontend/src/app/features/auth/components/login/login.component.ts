import {Component} from '@angular/core';
import {AuthService} from "../../../../core/services/auth.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private authService: AuthService, private router: Router) {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required)
    });
  }

  login() {
    this.authService.login(this.loginForm.value.email, this.loginForm.value.password).subscribe({
      next: (data) => {
        console.log(data.data);
        this.router.navigate(['/home']).then(r => console.log('Successful login'));
      },
      error: (error) => {
        console.log(error);
      }
    });
  }
}

