import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {Nullable} from "primeng/ts-helpers";
import {ProfileDto} from "../../features/user/models/profile.dto";
import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class ProfileService {
    baseUrl: string = `${environment.API_URL}/api/v1/profile`;

    constructor(private http: HttpClient, private utilService: UtilService) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    public getProfilePicture(): Observable<Blob> {
        return this.http.get<Blob>(`${this.baseUrl}/picture`, this.utilService.getHttpOptions('blob'));
    }

    public uploadProfilePicture(file: File): Observable<ResponseDto<Nullable>> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/picture`, formData, this.utilService.getHttpOptions());
    }

    public getProfile(): Observable<ResponseDto<ProfileDto>> {
        return this.http.get<ResponseDto<ProfileDto>>(`${this.baseUrl}`, this.utilService.getHttpOptions());
    }

    public updateProfile(firstName: string, lastName: string, phone: string, description: string): Observable<ResponseDto<Nullable>> {
        return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}`, {
            firstName, lastName, phone, description
        }, this.utilService.getHttpOptions());
    }

    public changePassword(oldPassword: string, password: string, confirmPassword: string): Observable<ResponseDto<Nullable>> {
        return this.http.put<ResponseDto<Nullable>>(`${this.baseUrl}/password`, {
            oldPassword, password, confirmPassword
        }, this.utilService.getHttpOptions());
    }
}
