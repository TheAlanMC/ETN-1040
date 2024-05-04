import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import { AuthDto } from '../../features/auth/models/auth.dto';
import {Nullable} from "primeng/ts-helpers";
import {PageDto} from "../models/page.dto";
import {UserDto} from "../../features/user/models/user.dto";
import {GroupDto} from "../../features/user/models/group.dto";
import {RoleDto} from "../../features/user/models/role.dto";

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  baseUrl: string = `${environment.API_URL}/api/v1/groups`;
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

  getGroups(): Observable<ResponseDto<GroupDto[]>> {
    return this.http.get<ResponseDto<GroupDto[]>>(this.baseUrl, this.getHttpOptions());
  }

  createGroup(groupName: string, groupDescription: string): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<Nullable>>(this.baseUrl, {name: groupName, description: groupDescription}, this.getHttpOptions());
  }

  updateGroup(groupId: number, groupName: string, groupDescription: string): Observable<ResponseDto<Nullable>> {
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/${groupId}`, {name: groupName, description: groupDescription}, this.getHttpOptions());
  }

  deleteGroup(groupId: number): Observable<ResponseDto<Nullable>> {
    return this.http.delete<ResponseDto<Nullable>>(`${this.baseUrl}/${groupId}`, this.getHttpOptions());
  }

  getGroupRoles(groupId: number): Observable<ResponseDto<RoleDto[]>> {
    return this.http.get<ResponseDto<RoleDto[]>>(`${this.baseUrl}/${groupId}/roles`, this.getHttpOptions());
  }

  addRolesToGroup(groupId: number, roleIds: number[]): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<Nullable>>(`${this.baseUrl}/${groupId}/roles`, {roleIds}, this.getHttpOptions());
  }
}

