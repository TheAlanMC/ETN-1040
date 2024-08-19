import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";

import {RoleDto} from "../../features/user/models/role.dto";
import {PermissionDto} from "../../features/user/models/permission.dto";
import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class RoleService {
    baseUrl: string = `${environment.API_URL}/api/v1/roles`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    getRoles(): Observable<ResponseDto<RoleDto[]>> {
        return this.http.get<ResponseDto<RoleDto[]>>(this.baseUrl,
            this.utilService.getHttpOptions());
    }

    createRole(
        roleName: string,
        roleDescription: string
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(this.baseUrl,
            {
                roleName, roleDescription
            },
            this.utilService.getHttpOptions());
    }

    updateRole(
        roleId: number,
        roleName: string,
        roleDescription: string
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${roleId}`,
            {
                roleName, roleDescription
            },
            this.utilService.getHttpOptions());
    }

    deleteRole(roleId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${roleId}`,
            this.utilService.getHttpOptions());
    }

    getRolePermissions(roleId: number): Observable<ResponseDto<PermissionDto[]>> {
        return this.http.get<ResponseDto<PermissionDto[]>>(`${this.baseUrl}/${roleId}/permissions`,
            this.utilService.getHttpOptions());
    }

    addPermissionsToRole(
        roleId: number,
        permissionIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/${roleId}/permissions`,
            {permissionIds},
            this.utilService.getHttpOptions());
    }
}

