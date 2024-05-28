import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {ProjectReportDto} from "../../features/report/models/project-report.dto";
import {TaskDashboardDto} from "../../features/home/models/task-dashboard.dto";
import {ProjectDashboardDto} from "../../features/home/models/project-dashboard.dto";


@Injectable({
    providedIn: 'root'
})
export class DashboardService {
    baseUrl: string = `${environment.API_URL}/api/v1/dashboards`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    public getTaskDashboard(
        dateFrom: string,
        dateTo: string,
    ): Observable<ResponseDto<TaskDashboardDto>> {
        return this.http.get<ResponseDto<TaskDashboardDto>>(`${this.baseUrl}/tasks?dateFrom=${dateFrom}&dateTo=${dateTo}`,
            this.utilService.getHttpOptions());
    }

    public getProjectDashboard(
        dateFrom: string,
        dateTo: string,
    ): Observable<ResponseDto<ProjectDashboardDto>> {
        return this.http.get<ResponseDto<ProjectDashboardDto>>(`${this.baseUrl}/projects?dateFrom=${dateFrom}&dateTo=${dateTo}`,
            this.utilService.getHttpOptions());
    }
}

