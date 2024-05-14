import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResponseDto} from "../models/response.dto";
import {UtilService} from "./util.service";
import {FileDto} from "../models/file.dto";

@Injectable({
  providedIn: 'root'
})
export class FileService {
  baseUrl: string = `${environment.API_URL}/api/v1/files`;

  constructor(private http: HttpClient, private utilService: UtilService) {
    this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
  }

  public getFile(fileId: string): Observable<Blob> {
    return this.http.get<Blob>(`${this.baseUrl}/${fileId}`, this.utilService.getHttpOptions('blob'));
  }

  public uploadFile(file: File): Observable<ResponseDto<FileDto>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ResponseDto<FileDto>>(`${this.baseUrl}`, formData, this.utilService.getHttpOptions());
  }
}
