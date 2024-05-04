import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {Nullable} from "primeng/ts-helpers";
import {ProfileDto} from "../../features/user/models/profile.dto";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  baseUrl: string = `${environment.API_URL}/api/v1/profile`;
  token: string = localStorage.getItem('token') || '';

  private getHttpOptions(responseType: 'json' | 'blob' = 'json'): Object {
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
      }),
      responseType
    };
  }

  constructor(private http: HttpClient) {}

  public getProfilePicture(): Observable<Blob> {
    return this.http.get<Blob>(`${this.baseUrl}/picture`, this.getHttpOptions('blob'));
  }

  public uploadProfilePicture(file: File): Observable<ResponseDto<Nullable>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/picture`, formData, this.getHttpOptions());
  }

  public getProfile(): Observable<ResponseDto<ProfileDto>> {
    return this.http.get<ResponseDto<ProfileDto>>(`${this.baseUrl}`, this.getHttpOptions());
  }

  public updateProfile(firstName: string, lastName: string, phone: string, description: string): Observable<ResponseDto<Nullable>> {
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}`, {firstName, lastName, phone, description}, this.getHttpOptions());
  }

  public changePassword(oldPassword: string, password: string, confirmPassword: string): Observable<ResponseDto<Nullable>> {
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/password`, {oldPassword, password, confirmPassword}, this.getHttpOptions());
  }
}
