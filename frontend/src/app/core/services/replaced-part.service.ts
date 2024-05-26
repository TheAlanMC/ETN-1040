import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class ReplacedPartService {
    baseUrl: string = `${environment.API_URL}/api/v1/replaced-parts`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    public createReplacedPart(
        taskId: number,
        replacedPartDescription: string,
        replacedPartFileIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(this.baseUrl,
            {
                taskId, replacedPartDescription, replacedPartFileIds
            },
            this.utilService.getHttpOptions());
    }

    public updateReplacedPart(
        replacedPartId: number,
        replacedPartDescription: string,
        replacedPartFileIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${replacedPartId}`,
            {
                replacedPartDescription, replacedPartFileIds
            },
            this.utilService.getHttpOptions());
    }

    public deleteReplacedPart(replacedPartId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${replacedPartId}`,
            this.utilService.getHttpOptions());
    }
}
