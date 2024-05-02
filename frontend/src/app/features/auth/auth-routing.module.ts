import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {ForgotPasswordComponent} from "./components/forgot-password/forgot-password.component";
import {NewPasswordComponent} from "./components/new-password/new-password.component";
import {VerificationComponent} from "./components/verification/verification.component";

const routes: Routes = [
  {path: 'auth',
    children: [
      {path: 'login', component: LoginComponent},
      {path: 'forgot-password', component: ForgotPasswordComponent},
      {path: 'verification', component: VerificationComponent},
      {path: 'new-password', component: NewPasswordComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
