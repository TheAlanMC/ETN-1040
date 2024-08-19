import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {PermissionDto} from "../../features/user/models/permission.dto";
import {UtilService} from "./util.service";

@Injectable({
    providedIn: 'root'
})
export class PermissionService {
    baseUrl: string = `${environment.API_URL}/api/v1/permissions`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    getPermissions(): Observable<ResponseDto<PermissionDto[]>> {
        return this.http.get<ResponseDto<PermissionDto[]>>(this.baseUrl,
            this.utilService.getHttpOptions());
    }
}

