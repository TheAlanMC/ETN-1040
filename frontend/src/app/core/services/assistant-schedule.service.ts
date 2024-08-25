import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";

import {UtilService} from "./util.service";
import {AssistantDto} from "../../features/schedule/models/assistant.dto";
import {AssistantScheduleDto} from "../../features/schedule/models/assistant-schedule.dto";

@Injectable({
    providedIn: 'root'
})
export class AssistantScheduleService {
    baseUrl: string = `${environment.API_URL}/api/v1/assistant-schedules`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    getSchedulesBySemesterId(semesterId: number): Observable<ResponseDto<AssistantDto[]>> {
        return this.http.get<ResponseDto<AssistantDto[]>>(`${this.baseUrl}/semesters/${semesterId}`,
            this.utilService.getHttpOptions());
    }

    getScheduleByAssistantId(assistantId: number): Observable<ResponseDto<AssistantDto>> {
        return this.http.get<ResponseDto<AssistantDto>>(`${this.baseUrl}/assistants/${assistantId}`,
            this.utilService.getHttpOptions());
    }

    createRandomSchedule(semesterId: number): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/semesters/${semesterId}/random`,
            {},
            this.utilService.getHttpOptions());
    }

    createCustomSchedule(
        semesterId: number,
        assistant: AssistantDto[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/semesters/${semesterId}/custom`,
            assistant,
            this.utilService.getHttpOptions());
    }

    getLastSemesterSchedule(): Observable<ResponseDto<AssistantScheduleDto[]>> {
        return this.http.get<ResponseDto<AssistantScheduleDto[]>>(`${this.baseUrl}/current`,
            this.utilService.getHttpOptions());
    }

}

