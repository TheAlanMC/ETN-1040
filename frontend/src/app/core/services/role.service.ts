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
export class RoleService {
  baseUrl: string = `${environment.API_URL}/api/v1/roles`;
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

  getRoles(): Observable<ResponseDto<RoleDto[]>> {
    return this.http.get<ResponseDto<RoleDto[]>>(this.baseUrl, this.getHttpOptions());
  }
}

