import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {Nullable} from "primeng/ts-helpers";
import {UserDto} from "../../features/user/models/user.dto";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  baseUrl: string = `${environment.API_URL}/api/v1/users`;
  token: string = localStorage.getItem('token') || '';

  constructor(private http: HttpClient) {}

  public getProfilePicture(): Observable<Blob> {
    const httpOptions : Object = {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
      }),
      responseType: 'blob'
    };
    return this.http.get<Blob>(`${this.baseUrl}/profile-picture`, httpOptions);
  }

  public uploadProfilePicture(file: File): Observable<ResponseDto<Nullable>> {
    const formData = new FormData();
    formData.append('file', file);
    const httpOptions : Object = {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
      })
    };
    return this.http.post<ResponseDto<Nullable>>(`${this.baseUrl}/profile-picture`, formData, httpOptions);
  }

  public getProfile(): Observable<ResponseDto<UserDto>> {
    const httpOptions : Object = {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
      })
    };
    return this.http.get<ResponseDto<UserDto>>(`${this.baseUrl}/profile`, httpOptions);
  }

  public updateProfile(firstName: string, lastName: string, phone: string, description: string): Observable<ResponseDto<Nullable>> {
    const httpOptions : Object = {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
      })
    };
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/profile`, {firstName, lastName, phone, description}, httpOptions);
  }

}
