import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {RoleDto} from "../../features/user/models/role.dto";
import {UtilService} from "./util.service";

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  baseUrl: string = `${environment.API_URL}/api/v1/roles`;

  constructor(private http: HttpClient, private utilService: UtilService) {
    if (this.utilService.checkIfMobile()) {
      this.baseUrl = this.baseUrl.replace('/backend', ':8080');
    }
  }

  getRoles(): Observable<ResponseDto<RoleDto[]>> {
    return this.http.get<ResponseDto<RoleDto[]>>(this.baseUrl, this.utilService.getHttpOptions());
  }
}

