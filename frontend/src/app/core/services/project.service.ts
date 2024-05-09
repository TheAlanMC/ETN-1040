import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {RoleDto} from "../../features/user/models/role.dto";
import {UtilService} from "./util.service";
import {Nullable} from "primeng/ts-helpers";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  baseUrl: string = `${environment.API_URL}/api/v1/projects`;

  constructor(private http: HttpClient, private utilService: UtilService) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
  }

  public createProject(projectName: string, projectDescription: string, dateFrom: string, dateTo: string, projectMemberIds: number[], projectModeratorIds: number[]): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<Nullable>>(this.baseUrl, {projectName, projectDescription, dateFrom, dateTo, projectMemberIds, projectModeratorIds}, this.utilService.getHttpOptions());
  }
}

