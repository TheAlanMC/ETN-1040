import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import { AuthDto } from '../../features/auth/models/auth.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  baseUrl: string = `${environment.API_URL}/api/v1/auth`;

  constructor(private http: HttpClient) {
  }

  //Login
  public login(email: string, password: string): Observable<ResponseDto<AuthDto>> {
    return this.http.post<ResponseDto<AuthDto>>(`${this.baseUrl}/login`, {email, password});
  }

  //Refresh token
  public refreshToken(refreshToken: string): Observable<ResponseDto<AuthDto>> {
    return this.http.post<ResponseDto<AuthDto>>(`${this.baseUrl}/refresh-token`, {refreshToken});
  }

}
