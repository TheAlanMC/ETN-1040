import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {Nullable} from "primeng/ts-helpers";
import {PageDto} from "../models/page.dto";
import {UserDto} from "../../features/user/models/user.dto";
import {GroupDto} from "../../features/user/models/group.dto";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  baseUrl: string = `${environment.API_URL}/api/v1/users`;
  token: string = localStorage.getItem('token') || '';

  private getHttpOptions(responseType: 'json' | 'blob' = 'json'): Object {
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
      }),
      responseType
    };
  }

  constructor(private http: HttpClient) {
  }

  public getUsers(sortBy: string, sortType: string, page: number, size: number, keyword: string = ''): Observable<ResponseDto<PageDto<UserDto>>> {
    return this.http.get<ResponseDto<PageDto<UserDto>>>(`${this.baseUrl}?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&keyword=${keyword}`, this.getHttpOptions());
  }

  public getUser(userId: number): Observable<ResponseDto<UserDto>> {
    return this.http.get<ResponseDto<UserDto>>(`${this.baseUrl}/${userId}`, this.getHttpOptions());
  }

  public createUser(groupId: number, email: string, firstName: string, lastName: string, phone: string, description: string): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<Nullable>>(`${this.baseUrl}`, {groupId, email, firstName, lastName, phone, description}, this.getHttpOptions());
  }

  public updateUser(userId: number, firstName: string, lastName: string, phone: string, description: string): Observable<ResponseDto<Nullable>> {
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/${userId}`, {firstName, lastName, phone, description}, this.getHttpOptions());
  }

  public deleteUser(userId: number): Observable<ResponseDto<Nullable>> {
    return this.http.delete<ResponseDto<Nullable>>(`${this.baseUrl}/${userId}`, this.getHttpOptions());
  }

  public getUserProfilePicture(userId: number): Observable<Blob> {
    return this.http.get<Blob>(`${this.baseUrl}/${userId}/profile-picture`, this.getHttpOptions('blob'));
  }

  public uploadUserProfilePicture(userId: number, file: File): Observable<ResponseDto<Nullable>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/${userId}/profile-picture`, formData, this.getHttpOptions());
  }

  public getUserGroups(userId: number): Observable<ResponseDto<GroupDto[]>> {
    return this.http.get<ResponseDto<GroupDto[]>>(`${this.baseUrl}/${userId}/groups`, this.getHttpOptions());
  }

  public addUsersToGroup(userId: number, groupIds: number []): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<Nullable>>(`${this.baseUrl}/${userId}/groups`, {groupIds}, this.getHttpOptions());
  }
}

