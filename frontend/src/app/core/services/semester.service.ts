import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {PermissionDto} from "../../features/user/models/permission.dto";
import {UtilService} from "./util.service";
import {ScheduleDto} from "../../features/schedule/models/schedule.dto";
import {SemesterDto} from "../../features/schedule/models/semester.dto";
import {AssistantDto} from "../../features/schedule/models/assistant.dto";

@Injectable({
    providedIn: 'root'
})
export class SemesterService {
    baseUrl: string = `${environment.API_URL}/api/v1/semesters`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    getSemesters(): Observable<ResponseDto<SemesterDto[]>> {
        return this.http.get<ResponseDto<SemesterDto[]>>(this.baseUrl,
            this.utilService.getHttpOptions());
    }

    getSemesterById(semesterId: number): Observable<ResponseDto<SemesterDto>> {
        return this.http.get<ResponseDto<SemesterDto>>(`${this.baseUrl}/${semesterId}`,
            this.utilService.getHttpOptions());

    }

    createSemester(semesterName: string, semesterDateFrom: string, semesterDateTo: string): Observable<ResponseDto<SemesterDto>> {
        return this.http.post<ResponseDto<SemesterDto>>(this.baseUrl, {
            semesterName,
            semesterDateFrom,
            semesterDateTo
        }, this.utilService.getHttpOptions());
    }

    updateSemester(semesterId: number, semesterName: string, semesterDateFrom: string, semesterDateTo: string): Observable<ResponseDto<SemesterDto>> {
        return this.http.put<ResponseDto<SemesterDto>>(`${this.baseUrl}/${semesterId}`, {
            semesterName,
            semesterDateFrom,
            semesterDateTo
        }, this.utilService.getHttpOptions());
    }

    deleteSemester(semesterId: number): Observable<ResponseDto<SemesterDto>> {
        return this.http.delete<ResponseDto<SemesterDto>>(`${this.baseUrl}/${semesterId}`,
            this.utilService.getHttpOptions());
    }

    getSemesterAssistants(semesterId: number): Observable<ResponseDto<AssistantDto[]>> {
        return this.http.get<ResponseDto<AssistantDto[]>>(`${this.baseUrl}/${semesterId}/assistants`,
            this.utilService.getHttpOptions());
    }

    addAssistantToSemester(semesterId: number, assistantIds: number[]): Observable<ResponseDto<AssistantDto>> {
        return this.http.post<ResponseDto<AssistantDto>>(`${this.baseUrl}/${semesterId}/assistants`, {
            assistantIds
        }, this.utilService.getHttpOptions());
    }


}

