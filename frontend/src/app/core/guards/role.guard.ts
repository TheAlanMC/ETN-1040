import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {jwtDecode} from 'jwt-decode';
import {JwtPayload} from "../models/jwt-payload.dto";

@Injectable({
    providedIn: 'root'
})
export class RoleGuard implements CanActivate {
    constructor(private router: Router) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
        const token = localStorage.getItem('token');
        // Check if token exists
        if (!token) {
            this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
        }
        const decoded = jwtDecode<JwtPayload>(token!);
        // Check if token is expired
        if (decoded.exp < Date.now() / 1000) {
            this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
        }
        // Extract roles from token
        const roles = decoded.roles
        const requiredRoles = route.data['roles'] as Array<string>;

        // Check if user has required roles
        if (roles.some(role => requiredRoles.includes(role))) {
            return true;
        } else {
            return this.router.parseUrl('/forbidden');
        }
    }
}
