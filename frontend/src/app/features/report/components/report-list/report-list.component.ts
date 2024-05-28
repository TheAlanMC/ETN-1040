import {Component, OnInit} from '@angular/core';
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {ReportDto} from '../../models/report.dto';
import {FormControl, Validators} from "@angular/forms";
import {ReportService} from "../../../../core/services/report.service";
import {FileService} from "../../../../core/services/file.service";
import {UtilService} from "../../../../core/services/util.service";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capacitor-community/file-opener";
import {FileDto} from "../../../../core/models/file.dto";
import {MessageService} from "primeng/api";

@Component({
    selector: 'app-report-list',
    templateUrl: './report-list.component.html',
    styleUrl: './report-list.component.scss',
    providers: [MessageService],
})
export class ReportListComponent implements OnInit {

    reports: ReportDto[] = [];

    // Pagination variables
    sortBy: string = 'reportId';
    sortType: string = 'asc';
    page: number = 0;
    size: number = 10;

    dateFromControl = new FormControl('',[Validators.required]);
    dateToControl = new FormControl('',[Validators.required]);

    totalElements: number = 0;

    isDataLoading: boolean = true;

    isMobile: boolean = false;

    isLoading: boolean = false;

    downloadingFileId: number = 0;

    constructor(
        private reportService: ReportService,
        private fileService: FileService,
        private utilService: UtilService,
        private messageService: MessageService
    ) {
        this.isMobile = this.utilService.checkIfMobile();
    }

    ngOnInit() {
        this.isDataLoading = false;
    }

    public onPageChange(event: any) {
        const first = event.first;
        const rows = event.rows;
        this.page = Math.floor(first / rows);
        this.size = rows;
        this.getData();
    }

    public onSortChange(event: any) {
        this.sortBy = event.field;
        this.sortType = (event.order == 1) ? 'asc' : 'desc';
        this.getData();
    }

    public getData() {
        this.isDataLoading = true;
        this.reportService.getReports(
            this.sortBy,
            this.sortType,
            this.page,
            this.size,
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
        ).subscribe({
            next: (data: ResponseDto<PageDto<ReportDto>>) => {
                this.reports = data.data!.content;
                this.totalElements = data.data!.page.totalElements;
                this.isDataLoading = false;
            }, error: (error) => {
                console.error(error);
                this.isDataLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onDownloadReport(file: FileDto) {
        this.isLoading = true;
        this.downloadingFileId = file.fileId;
        this.fileService.getFile(file.fileId).subscribe({
            next: (data) => {
                const blob = new Blob([data],
                    {type: data.type});
                if (!this.isMobile) {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = file.fileName;
                    document.body.appendChild(a);
                    a.click();
                    URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                } else {
                    const reader = new FileReader();
                    reader.onloadend = async () => {
                        const base64Data = reader.result as string;
                        const savedFile = await Filesystem.writeFile({
                            path: file.fileName,
                            data: base64Data,
                            directory: Directory.Documents,
                        });
                        const fileOpenerOptions = {
                            filePath: savedFile.uri,
                            contentType: file.contentType,
                            openWithDefault: true,
                        };
                        await FileOpener.open(fileOpenerOptions);
                    };
                    reader.readAsDataURL(blob);
                }
                this.isLoading = false;
                this.downloadingFileId = 0;
            }, error: (error) => {
                console.error(error);
            }
        });
    }
}

