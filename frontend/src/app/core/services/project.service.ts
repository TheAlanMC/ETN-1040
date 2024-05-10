import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {RoleDto} from "../../features/user/models/role.dto";
import {UtilService} from "./util.service";
import {Nullable} from "primeng/ts-helpers";
import {PageDto} from "../models/page.dto";
import {ProjectDto} from "../../features/project/models/project.dto";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  baseUrl: string = `${environment.API_URL}/api/v1/projects`;

  constructor(private http: HttpClient, private utilService: UtilService) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
  }

  public getProjects(sortBy: string, sortType: string, page: number, size: number): Observable<ResponseDto<PageDto<ProjectDto>>> {
    return this.http.get<ResponseDto<PageDto<ProjectDto>>>(`${this.baseUrl}?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}`, this.utilService.getHttpOptions());
  }

  public getProject(projectId: number): Observable<ResponseDto<ProjectDto>> {
    return this.http.get<ResponseDto<ProjectDto>>(`${this.baseUrl}/${projectId}`, this.utilService.getHttpOptions());
  }

  public createProject(projectName: string, projectDescription: string, dateFrom: string, dateTo: string, projectMemberIds: number[], projectModeratorIds: number[]): Observable<ResponseDto<Nullable>> {
    return this.http.post<ResponseDto<Nullable>>(this.baseUrl, {projectName, projectDescription, dateFrom, dateTo, projectMemberIds, projectModeratorIds}, this.utilService.getHttpOptions());
  }

  public updateProject(projectId: number, projectName: string, projectDescription: string, dateFrom: string, dateTo: string, projectMemberIds: number[], projectModeratorIds: number[]): Observable<ResponseDto<Nullable>> {
    return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/${projectId}`, {projectName, projectDescription, dateFrom, dateTo, projectMemberIds, projectModeratorIds}, this.utilService.getHttpOptions());
  }

  public deleteProject(projectId: number): Observable<ResponseDto<Nullable>> {
    return this.http.delete<ResponseDto<Nullable>>(`${this.baseUrl}/${projectId}`, this.utilService.getHttpOptions());
  }
}

