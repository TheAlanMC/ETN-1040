import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {NotificationDto} from "../models/notification.dto";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    baseUrl: string = `${environment.API_URL}/api/v1/notifications`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    getNotifications(): Observable<ResponseDto<NotificationDto[]>> {
        return this.http.get<ResponseDto<NotificationDto[]>>(this.baseUrl,
            this.utilService.getHttpOptions());
    }

    markAsRead(notificationId: number): Observable<ResponseDto<NotificationDto>> {
        return this.http.put<ResponseDto<NotificationDto>>(`${this.baseUrl}/${notificationId}`,
            null,
            this.utilService.getHttpOptions());
    }
}

