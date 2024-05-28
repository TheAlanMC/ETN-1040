import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";

import {PageDto} from "../models/page.dto";
import {ProjectDto} from "../../features/project/models/project.dto";
import {TaskDto} from "../../features/task/models/task.dto";

@Injectable({
    providedIn: 'root'
})
export class ProjectService {
    baseUrl: string = `${environment.API_URL}/api/v1/projects`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    public getAllProjects(): Observable<ResponseDto<ProjectDto[]>> {
        return this.http.get<ResponseDto<ProjectDto[]>>(`${this.baseUrl}/all`,
            this.utilService.getHttpOptions());
    }

    public getProjects(
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        keyword: string
    ): Observable<ResponseDto<PageDto<ProjectDto>>> {
        return this.http.get<ResponseDto<PageDto<ProjectDto>>>(`${this.baseUrl}?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&keyword=${keyword}`,
            this.utilService.getHttpOptions());
    }

    public getProject(projectId: number): Observable<ResponseDto<ProjectDto>> {
        return this.http.get<ResponseDto<ProjectDto>>(`${this.baseUrl}/${projectId}`,
            this.utilService.getHttpOptions());
    }

    public createProject(
        projectName: string,
        projectDescription: string,
        projectDateFrom: string,
        projectDateTo: string,
        projectObjective: string,
        projectMemberIds: number[],
        projectModeratorIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(this.baseUrl,
            {
                projectName,
                projectDescription,
                projectDateFrom,
                projectDateTo,
                projectObjective,
                projectMemberIds,
                projectModeratorIds
            },
            this.utilService.getHttpOptions());
    }

    public updateProject(
        projectId: number,
        projectName: string,
        projectDescription: string,
        projectDateFrom: string,
        projectDateTo: string,
        projectObjective: string,
        projectMemberIds: number[],
        projectModeratorIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${projectId}`,
            {
                projectName,
                projectDescription,
                projectDateFrom,
                projectDateTo,
                projectObjective,
                projectMemberIds,
                projectModeratorIds
            },
            this.utilService.getHttpOptions());
    }

    public deleteProject(projectId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${projectId}`,
            this.utilService.getHttpOptions());
    }

    public closeProject(
        projectId: number,
        projectCloseMessage: string
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${projectId}/close`,
            {projectCloseMessage},
            this.utilService.getHttpOptions());
    }

    public getProjectTasks(
        projectId: number,
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        keyword: string,
        statuses: string[],
        priorities: string[],
        dateFrom: string | null = null,
        dateTo: string | null = null
    ): Observable<ResponseDto<PageDto<TaskDto>>> {
        const statusList = statuses.join(',');
        const priorityList = priorities.join(',');
        return this.http.get<ResponseDto<PageDto<TaskDto>>>(`${this.baseUrl}/${projectId}/tasks?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&keyword=${keyword}&statuses=${statusList}&priorities=${priorityList}&dateFrom=${(dateFrom ? dateFrom : '')}&dateTo=${(dateTo ? dateTo : '')}`,
            this.utilService.getHttpOptions());
    }
}

