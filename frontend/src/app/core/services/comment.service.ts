import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {FileDto} from "../models/file.dto";
import {TaskStatusDto} from "../../features/task/models/task-status.dto";
import {PageDto} from "../models/page.dto";
import {ProjectDto} from "../../features/project/models/project.dto";
import {TaskDto} from "../../features/task/models/task.dto";
import {TaskCommentDto} from "../../features/task/models/task-comment.dto";

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  baseUrl: string = `${environment.API_URL}/api/v1/task-comments`;

  constructor(private http: HttpClient, private utilService: UtilService) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
  }

  public getTaskComments(taskId: number): Observable<ResponseDto<TaskCommentDto[]>> {
    return this.http.get<ResponseDto<TaskCommentDto[]>>(`${this.baseUrl}/${taskId}`, this.utilService.getHttpOptions());
  }

  public createTaskComment(taskId: number, comment: string, taskCommentFileIds: number[]): Observable<ResponseDto<null>> {
    return this.http.post<ResponseDto<null>>(this.baseUrl, {
      taskId,
      comment,
      taskCommentFileIds
    }, this.utilService.getHttpOptions());
  }

  public updateTaskComment(taskCommentId: number, comment: string, taskCommentFileIds: number[]): Observable<ResponseDto<null>> {
    return this.http.put<ResponseDto<null>>(`${this.baseUrl}/${taskCommentId}`, {
      comment,
      taskCommentFileIds
    }, this.utilService.getHttpOptions());
  }

  public deleteTaskComment(taskCommentId: number): Observable<ResponseDto<null>> {
    return this.http.delete<ResponseDto<null>>(`${this.baseUrl}/${taskCommentId}`, this.utilService.getHttpOptions());
  }
}
