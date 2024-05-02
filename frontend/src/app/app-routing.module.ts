import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {NotFoundComponent} from "./shared/components/not-found/not-found.component";
import {ForbiddenComponent} from "./shared/components/forbidden/forbidden.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},

  { path: '', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { path: '', loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule) },

  // 404 Not Found page
  { path: 'not-found', component: NotFoundComponent },
  // 403 Forbidden page
  { path: 'forbidden', component: ForbiddenComponent },
  // Redirect to 404 for any unknown paths
  { path: '**', redirectTo: 'not-found' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
