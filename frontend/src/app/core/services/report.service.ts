import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {ReportDto} from "../../features/report/models/report.dto";
import {PageDto} from "../models/page.dto";
import {TaskReportFilterDto} from "../../features/report/models/task-report-filter.dto";
import {TaskReportDto} from "../../features/report/models/task-report.dto";
import {ProjectReportDto} from "../../features/report/models/project-report.dto";
import {ExecutiveReportDto} from "../../features/report/models/executive-report.dto";
import {ProjectReportFilterDto} from "../../features/report/models/project-report-filter.dto";


@Injectable({
    providedIn: 'root'
})
export class ReportService {
    baseUrl: string = `${environment.API_URL}/api/v1/reports`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    public getReports(
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        dateFrom: string,
        dateTo: string,
    ): Observable<ResponseDto<PageDto<ReportDto>>> {
        return this.http.get<ResponseDto<PageDto<ReportDto>>>(`${this.baseUrl}?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&dateFrom=${dateFrom}&dateTo=${dateTo}`,
            this.utilService.getHttpOptions());
    }

    public uploadReport(
        dateFrom: string,
        dateTo: string,
        reportType: string,
        fileId: number,
        fileName: string,
        contentType: string,
        fileSize: number,
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/${reportType}?dateFrom=${dateFrom}&dateTo=${dateTo}`,
            {
                fileId, fileName, contentType, fileSize,
            },
            this.utilService.getHttpOptions());
    }

    public getTaskFilters(
        dateFrom: string,
        dateTo: string,
    ): Observable<ResponseDto<TaskReportFilterDto>> {
        return this.http.get<ResponseDto<TaskReportFilterDto>>(`${this.baseUrl}/tasks/filters?dateFrom=${dateFrom}&dateTo=${dateTo}`,
            this.utilService.getHttpOptions());
    }

    public getTaskReport(
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        dateFrom: string,
        dateTo: string,
        projects: number[],
        taskAssignees: number[],
        statuses: string[],
        priorities: string[],
    ): Observable<ResponseDto<PageDto<TaskReportDto>>> {
        const projectList = projects.join(',');
        const taskAssigneeList = taskAssignees.join(',');
        const statusList = statuses.join(',');
        const priorityList = priorities.join(',');
        return this.http.get<ResponseDto<PageDto<TaskReportDto>>>(`${this.baseUrl}/tasks?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&dateFrom=${dateFrom}&dateTo=${dateTo}&projects=${projectList}&taskAssignees=${taskAssigneeList}&statuses=${statusList}&priorities=${priorityList}`,
            this.utilService.getHttpOptions());
    }

    public getProjectFilters(
        dateFrom: string,
        dateTo: string,
    ): Observable<ResponseDto<ProjectReportFilterDto>> {
        return this.http.get<ResponseDto<ProjectReportFilterDto>>(`${this.baseUrl}/projects/filters?dateFrom=${dateFrom}&dateTo=${dateTo}`,
            this.utilService.getHttpOptions());
    }

    public getProjectReport(
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        dateFrom: string,
        dateTo: string,
        projectOwners: number[],
        projectModerators: number[],
        projectMembers: number[],
        statuses: string[],
    ): Observable<ResponseDto<PageDto<ProjectReportDto>>> {
        const projectOwnerList = projectOwners.join(',');
        const projectModeratorList = projectModerators.join(',');
        const projectMemberList = projectMembers.join(',');
        const statusList = statuses.join(',');
        return this.http.get<ResponseDto<PageDto<ProjectReportDto>>>(`${this.baseUrl}/projects?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&dateFrom=${dateFrom}&dateTo=${dateTo}&projectOwners=${projectOwnerList}&projectModerators=${projectModeratorList}&projectMembers=${projectMemberList}&statuses=${statusList}`,
            this.utilService.getHttpOptions());
    }

    public getExecutiveReport(
        dateFrom: string,
        dateTo: string,
    ): Observable<ResponseDto<ExecutiveReportDto>> {
        return this.http.get<ResponseDto<ExecutiveReportDto>>(`${this.baseUrl}/executives?dateFrom=${dateFrom}&dateTo=${dateTo}`,
            this.utilService.getHttpOptions());
    }
}
