import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {ScheduleDto} from "../../features/schedule/models/schedule.dto";

@Injectable({
    providedIn: 'root'
})
export class ScheduleService {
    baseUrl: string = `${environment.API_URL}/api/v1/schedules`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    getSchedules(): Observable<ResponseDto<ScheduleDto[]>> {
        return this.http.get<ResponseDto<ScheduleDto[]>>(this.baseUrl,
            this.utilService.getHttpOptions());
    }

}

