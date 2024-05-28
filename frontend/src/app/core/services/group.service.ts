import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";

import {GroupDto} from "../../features/user/models/group.dto";
import {RoleDto} from "../../features/user/models/role.dto";
import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class GroupService {
    baseUrl: string = `${environment.API_URL}/api/v1/groups`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    getGroups(): Observable<ResponseDto<GroupDto[]>> {
        return this.http.get<ResponseDto<GroupDto[]>>(this.baseUrl,
            this.utilService.getHttpOptions());
    }

    createGroup(
        groupName: string,
        groupDescription: string
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(this.baseUrl,
            {
                groupName, groupDescription
            },
            this.utilService.getHttpOptions());
    }

    updateGroup(
        groupId: number,
        groupName: string,
        groupDescription: string
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${groupId}`,
            {
                groupName, groupDescription
            },
            this.utilService.getHttpOptions());
    }

    deleteGroup(groupId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${groupId}`,
            this.utilService.getHttpOptions());
    }

    getGroupRoles(groupId: number): Observable<ResponseDto<RoleDto[]>> {
        return this.http.get<ResponseDto<RoleDto[]>>(`${this.baseUrl}/${groupId}/roles`,
            this.utilService.getHttpOptions());
    }

    addRolesToGroup(
        groupId: number,
        roleIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/${groupId}/roles`,
            {roleIds},
            this.utilService.getHttpOptions());
    }
}

