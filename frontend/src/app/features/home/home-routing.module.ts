import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomePageComponent} from "./components/home-page/home-page.component";
import {AuthGuard} from "../../core/guards/auth.guard";
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {RoleGuard} from "../../core/guards/role.guard";

const routes: Routes = [
    {
        path: '', children: [
            {path: '', component: HomePageComponent, canActivate: [AuthGuard]},
            {
                path: 'dashboard',
                component: DashboardComponent,
                canActivate: [RoleGuard],
                data: {roles: ['VER DASHBOARD'], breadcrumb: 'Dashboard'}
            }
        ]
    }
];


@NgModule({
    imports: [RouterModule.forChild(routes)], exports: [RouterModule]
})
export class HomeRoutingModule {
}
