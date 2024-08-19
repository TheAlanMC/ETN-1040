import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";

import {PageDto} from "../models/page.dto";
import {UserDto} from "../../features/user/models/user.dto";
import {RoleDto} from "../../features/user/models/role.dto";
import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class UserService {
    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    public getAllUsers(): Observable<ResponseDto<UserDto[]>> {
        return this.http.get<ResponseDto<UserDto[]>>(`${this.baseUrl}/all`,
            this.utilService.getHttpOptions());
    }

    public getUsers(
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        keyword: string = ''
    ): Observable<ResponseDto<PageDto<UserDto>>> {
        return this.http.get<ResponseDto<PageDto<UserDto>>>(`${this.baseUrl}?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&keyword=${keyword}`,
            this.utilService.getHttpOptions());
    }

    public getUser(userId: number): Observable<ResponseDto<UserDto>> {
        return this.http.get<ResponseDto<UserDto>>(`${this.baseUrl}/${userId}`,
            this.utilService.getHttpOptions());
    }

    public createUser(
        roleId: number,
        email: string,
        firstName: string,
        lastName: string,
        phone: string,
        description: string
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}`,
            {
                roleId, email, firstName, lastName, phone, description
            },
            this.utilService.getHttpOptions());
    }

    public updateUser(
        userId: number,
        firstName: string,
        lastName: string,
        phone: string,
        description: string
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${userId}`,
            {
                firstName, lastName, phone, description
            },
            this.utilService.getHttpOptions());
    }

    public deleteUser(userId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${userId}`,
            this.utilService.getHttpOptions());
    }

    public getUserProfilePicture(userId: number): Observable<Blob> {
        return this.http.get<Blob>(`${this.baseUrl}/${userId}/profile-picture`,
            this.utilService.getHttpOptions('blob'));
    }

    public uploadUserProfilePicture(
        userId: number,
        file: File
    ): Observable<ResponseDto<null>> {
        const formData = new FormData();
        formData.append('file',
            file);
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${userId}/profile-picture`,
            formData,
            this.utilService.getHttpOptions());
    }

    public getUserRoles(userId: number): Observable<ResponseDto<RoleDto[]>> {
        return this.http.get<ResponseDto<RoleDto[]>>(`${this.baseUrl}/${userId}/roles`,
            this.utilService.getHttpOptions());
    }

    public addUsersToRole(
        userId: number,
        roleIds: number []
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/${userId}/roles`,
            {roleIds},
            this.utilService.getHttpOptions());
    }
}
