import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {ReportService} from "../../../../core/services/report.service";
import {FileService} from "../../../../core/services/file.service";
import {MessageService} from "primeng/api";
import {ExecutiveReportDto} from "../../models/executive-report.dto";
import {ProjectReportDto} from "../../models/project-report.dto";
import {PdfGenerationService} from "../../../../core/services/pdf-generation.service";
import {FileDto} from "../../../../core/models/file.dto";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capacitor-community/file-opener";
import {UtilService} from "../../../../core/services/util.service";

@Component({
    selector: 'app-executive-report',
    templateUrl: './executive-report.component.html',
    styleUrl: './executive-report.component.scss',
    providers: [MessageService],
})
export class ExecutiveReportComponent implements OnInit {

    isLoading: boolean = false;

    executiveReport: ExecutiveReportDto | null = null;

    file: FileDto | null = null;


    dateFromControl = new FormControl('',
        [Validators.required]);
    dateToControl = new FormControl('',
        [Validators.required]);

    isDataLoading: boolean = true;

    generatedReport: boolean = false;

    expandedRows = {};

    isMobile: boolean = false;

    constructor(
        private reportService: ReportService,
        private fileService: FileService,
        private messageService: MessageService,
        private pdfGenerationService: PdfGenerationService,
        private utilService: UtilService,
    ) {
        this.isMobile = this.utilService.checkIfMobile();
    }

    ngOnInit() {
        this.isDataLoading = false;
    }


    public getData() {
        this.isDataLoading = true;
        this.reportService.getExecutiveReport(
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
        ).subscribe({
            next: (data) => {
                this.executiveReport = data.data;
                this.isDataLoading = false;
                this.generatedReport = true;
            }, error: (error) => {
                console.error(error);
                this.isDataLoading = false;

                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onSync() {
        this.getData();
    }

    public onExportPdf() {
        this.isLoading = true;
        this.pdfGenerationService.generatePdf('executiveReportContent',
            'Reporte Ejecutivo.pdf').subscribe(
            {
                next: (pdf) => {
                    this.upLoadFile(pdf);
                },
                error: (error) => {
                    console.error(error);
                    this.isLoading = false;
                    this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
                }
            }
        );
    }

    public upLoadFile(file: File) {
        this.isLoading = true;
        this.fileService.uploadFile(file).subscribe({
            next: (response) => {
                this.file = response.data;
                this.saveReport();
            },
            error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public saveReport() {
        this.reportService.uploadReport(
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
            'EJECUTIVO',
            this.file!.fileId,
            this.file!.fileName,
            this.file!.contentType,
            this.file!.fileSize,
        ).subscribe({
            next: (response) => {
                this.downloadReport();
                this.isLoading = false;
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: response.message});
            },
            error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public downloadReport() {
        this.fileService.getFile(this.file!.fileId).subscribe({
            next: (data) => {
                const blob = new Blob([data],
                    {type: data.type});
                if (!this.isMobile) {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = this.file!.fileName;
                    document.body.appendChild(a);
                    a.click();
                    URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                } else {
                    const reader = new FileReader();
                    reader.onloadend = async () => {
                        const base64Data = reader.result as string;
                        const savedFile = await Filesystem.writeFile({
                            path: this.file!.fileName,
                            data: base64Data,
                            directory: Directory.Documents,
                        });
                        const fileOpenerOptions = {
                            filePath: savedFile.uri,
                            contentType: this.file!.contentType,
                            openWithDefault: true,
                        };
                        await FileOpener.open(fileOpenerOptions);
                    };
                    reader.readAsDataURL(blob);
                }
            },
            error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onClear() {
        this.generatedReport = false;
        this.executiveReport = null;
        this.dateFromControl.reset();
        this.dateToControl.reset();
    }

    expandAll() {
        this.expandedRows = this.executiveReport?.projectReports.reduce((
                acc: any,
                project: ProjectReportDto
            ) => (acc[project.projectId] = true) && acc,
            {});
    }

    collapseAll() {
        this.expandedRows = {};
    }


}
