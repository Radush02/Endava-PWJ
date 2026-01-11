import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { AuthGuard } from './core/auth/auth.guard';
import { notAuthGuard } from './core/auth/not-auth.guard';
import { ProblemHomeComponent } from './features/problem/problem-home/problem-home.component';
import { ProblemViewComponent } from './features/problem/problem-view/problem-view.component';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full',canMatch: [AuthGuard]},
    {path:'home',component:ProblemHomeComponent, canMatch: [AuthGuard]},
    { path: 'login',component: LoginComponent,canMatch: [notAuthGuard]},
    {path: 'register', component:RegisterComponent, canMatch: [notAuthGuard]},
    {path:"problem/:id",component:ProblemViewComponent, canMatch: [AuthGuard]},
];
