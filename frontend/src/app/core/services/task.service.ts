import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {TaskStatusDto} from "../../features/task/models/task-status.dto";
import {PageDto} from "../models/page.dto";
import {TaskDto} from "../../features/task/models/task.dto";
import {TaskHistoryDto} from "../../features/task/models/task-history.dto";

@Injectable({
    providedIn: 'root'
})
export class TaskService {
    baseUrl: string = `${environment.API_URL}/api/v1/tasks`;

    constructor(
        private http: HttpClient,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    public getStatuses(): Observable<ResponseDto<TaskStatusDto[]>> {
        return this.http.get<ResponseDto<TaskStatusDto[]>>(`${this.baseUrl}/statuses`,
            this.utilService.getHttpOptions());
    }

    public getTasks(
        sortBy: string,
        sortType: string,
        page: number,
        size: number,
        keyword: string,
        statuses: string[],
        dateFrom: string | null = null,
        dateTo: string | null = null
    ): Observable<ResponseDto<PageDto<TaskDto>>> {
        const statusList = statuses.join(',');
        return this.http.get<ResponseDto<PageDto<TaskDto>>>(`${this.baseUrl}?sortBy=${sortBy}&sortType=${sortType}&page=${page}&size=${size}&keyword=${keyword}&statuses=${statusList}&dateFrom=${(dateFrom ? dateFrom : '')}&dateTo=${(dateTo ? dateTo : '')}`,
            this.utilService.getHttpOptions());
    }

    public getTask(taskId: number): Observable<ResponseDto<TaskDto>> {
        return this.http.get<ResponseDto<TaskDto>>(`${this.baseUrl}/${taskId}`,
            this.utilService.getHttpOptions());
    }

    public createTask(
        projectId: number,
        taskName: string,
        taskDescription: string,
        taskDueDate: string,
        taskPriority: number,
        taskAssigneeIds: number[],
        taskFileIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(this.baseUrl,
            {
                projectId, taskName, taskDescription, taskDueDate, taskPriority, taskAssigneeIds, taskFileIds
            },
            this.utilService.getHttpOptions());
    }

    public updateTask(
        taskId: number,
        taskName: string,
        taskDescription: string,
        taskDueDate: string,
        taskPriority: number,
        taskAssigneeIds: number[],
        taskFileIds: number[]
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${taskId}`,
            {
                taskName, taskDescription, taskDueDate, taskPriority, taskAssigneeIds, taskFileIds
            },
            this.utilService.getHttpOptions());
    }

    public updateTaskStatus(
        taskId: number,
        taskStatusId: number,
        taskStatusName: string
    ): Observable<ResponseDto<null>> {
        return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${taskId}/status`,
            {
                taskStatusId, taskStatusName
            },
            this.utilService.getHttpOptions());
    }

    public deleteTask(taskId: number): Observable<ResponseDto<null>> {
        return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${taskId}`,
            this.utilService.getHttpOptions());
    }

    public createTaskFeedback(
        taskId: number,
        rating: number,
        feedback: string
    ): Observable<ResponseDto<null>> {
        return this.http.post<ResponseDto<null>>(`${this.baseUrl}/${taskId}/feedback`,
            {
                rating, feedback
            },
            this.utilService.getHttpOptions());
    }

    public getTaskHistory(taskId: number): Observable<ResponseDto<TaskHistoryDto[]>> {
        return this.http.get<ResponseDto<TaskHistoryDto[]>>(`${this.baseUrl}/${taskId}/history/all`,
            this.utilService.getHttpOptions());
    }
}
