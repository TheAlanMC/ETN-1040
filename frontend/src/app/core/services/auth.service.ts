import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import { AuthDto } from '../../features/auth/models/auth.dto';
import {Nullable} from "primeng/ts-helpers";
import {UtilService} from "./util.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  baseUrl: string = `${environment.API_URL}/api/v1/auth`;

  constructor(private http: HttpClient, private device: UtilService) {
    if (this.device.checkIfMobile()) {
      this.baseUrl = this.baseUrl.replace('/backend', ':8080');
    }
  }

  public login(email: string, password: string): Observable<ResponseDto<AuthDto>> {
    return this.http.post<ResponseDto<AuthDto>>(`${this.baseUrl}/login`, {email, password});
  }

  public refreshToken(refreshToken: string): Observable<ResponseDto<AuthDto>> {
    return this.http.post<ResponseDto<AuthDto>>(`${this.baseUrl}/refresh-token`, {refreshToken});
  }

  public forgotPassword(email: string): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<any>>(`${this.baseUrl}/forgot-password`, {email});
  }

  public verification(email: string, code: string): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<any>>(`${this.baseUrl}/verification`, {email, code});
  }

  public resetPassword(email: string, code: string, password: string, confirmPassword: string): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<any>>(`${this.baseUrl}/reset-password`, {email, code, password, confirmPassword});
  }


}
