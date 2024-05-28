import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {AuthDto} from '../../features/auth/models/auth.dto';

import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    baseUrl: string = `${environment.API_URL}/api/v1/auth`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService,
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    public login(
        email: string,
        password: string,
        firebaseToken: string
    ): Observable<ResponseDto<AuthDto>> {
        return this.http.post<ResponseDto<AuthDto>>(`${this.baseUrl}/login`,
            {email, password, firebaseToken});
    }

    public refreshToken(refreshToken: string): Observable<ResponseDto<AuthDto>> {
        return this.http.post<ResponseDto<AuthDto>>(`${this.baseUrl}/refresh-token`,
            {refreshToken});
    }

    public forgotPassword(email: string): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<any>>(`${this.baseUrl}/forgot-password`,
            {email});
    }

    public verification(
        email: string,
        code: string
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<any>>(`${this.baseUrl}/verification`,
            {email, code});
    }

    public resetPassword(
        email: string,
        code: string,
        password: string,
        confirmPassword: string
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<any>>(`${this.baseUrl}/reset-password`,
            {
                email,
                code,
                password,
                confirmPassword
            });
    }


}
