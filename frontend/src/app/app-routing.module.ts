import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NotFoundComponent} from "./shared/components/not-found/not-found.component";
import {ForbiddenComponent} from "./shared/components/forbidden/forbidden.component";
import {AppLayoutComponent} from "./layout/app.layout.component";

const routes: Routes = [
    {
        path: '', component: AppLayoutComponent, children: [
            {
                path: '',
                data: {breadcrumb: 'Inicio'},
                loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule)
            },
            {
                path: '',
                data: {breadcrumb: 'Horario'},
                loadChildren: () => import('./features/schedule/schedule.module').then(m => m.ScheduleModule)
            },
            {
                path: '',
                data: {breadcrumb: 'Proyectos'},
                loadChildren: () => import('./features/project/project.module').then(m => m.ProjectModule)
            },
            {
                path: '',
                data: {breadcrumb: 'Reportes'},
                loadChildren: () => import('./features/report/report.module').then(m => m.ReportModule)
            },
            {
                path: '',
                data: {breadcrumb: 'Tareas'},
                loadChildren: () => import('./features/task/task.module').then(m => m.TaskModule)
            },
            {
                path: '',
                data: {breadcrumb: 'Usuarios'},
                loadChildren: () => import('./features/user/user.module').then(m => m.UserModule)
            },
        ]
    },
    // Auth pages
    {path: '', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)},
    // 404 Not Found page
    {path: 'not-found', component: NotFoundComponent},
    // 403 Forbidden page
    {path: 'forbidden', component: ForbiddenComponent},
    // Redirect to NotFound page
    {path: '**', redirectTo: 'not-found'}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
