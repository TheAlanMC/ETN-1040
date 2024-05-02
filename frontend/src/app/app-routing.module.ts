import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {NotFoundComponent} from "./shared/components/not-found/not-found.component";
import {ForbiddenComponent} from "./shared/components/forbidden/forbidden.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},

  { path: '', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { path: '', loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule) },

  // 404 Not Found page
  { path: '404', component: NotFoundComponent },
  // 403 Forbidden page
  { path: '403', component: ForbiddenComponent },
  // Redirect to 404 for any unknown paths
  { path: '**', redirectTo: '404' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
