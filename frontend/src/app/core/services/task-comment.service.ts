import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";


@Injectable({
    providedIn: 'root'
})
export class TaskCommentService {
    baseUrl: string = `${environment.API_URL}/api/v1/task-comments`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
    }

    public createTaskComment(
        taskId: number,
        taskComment: string,
        taskCommentFileIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(this.baseUrl,
            {
                taskId, taskComment, taskCommentFileIds
            },
            this.utilService.getHttpOptions());
    }

    public updateTaskComment(
        taskCommentId: number,
        taskComment: string,
        taskCommentFileIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${taskCommentId}`,
            {
                taskComment, taskCommentFileIds
            },
            this.utilService.getHttpOptions());
    }

    public deleteTaskComment(taskCommentId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${taskCommentId}`,
            this.utilService.getHttpOptions());
    }
}
