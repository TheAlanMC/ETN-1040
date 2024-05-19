import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Router} from '@angular/router';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

    constructor(private router: Router) {
    }

    intercept(
        request: HttpRequest<any>,
        next: HttpHandler
    ) {
        return next.handle(request).pipe(catchError((error: HttpErrorResponse) => {
            //this.showMessage(`Error: ${error.status} - ${error.error.message}`);
            if (error.status === 404) {
                this.router.navigate(['/not-found']).then(r => console.log('Not Found, redirect to not found page'));
            } else if (error.status === 403) {
                this.router.navigate(['/forbidden']).then(r => console.log('Forbidden, redirect to forbidden page'));
            } else if (error.status === 401) {
                this.router.navigate(['/auth/login']).then(r => console.log('Unauthorized, redirect to login page'));
            }
            return throwError(error);
        }));
    }


}
