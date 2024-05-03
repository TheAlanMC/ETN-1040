import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import {JwtPayload} from "../models/jwt-payload.dto";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean | UrlTree {
    const token = localStorage.getItem('token');
    // Check if token exists
    if (!token) {
      this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
      return false;
    }
    const decoded = jwtDecode<JwtPayload>(token!!);
    // Check if token is expired
    if (decoded.exp < Date.now() / 1000) {
      this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
      return false;
    }
    return true;
  }
}
