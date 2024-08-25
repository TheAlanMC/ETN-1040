import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {ReportService} from "../../../../core/services/report.service";
import {MessageService} from "primeng/api";
import {ExecutiveReportDto} from "../../models/executive-report.dto";
import {ProjectReportDto} from "../../models/project-report.dto";
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
        private messageService: MessageService,
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
        this.reportService.getExecutiveReportPdf(
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
        ).subscribe({
            next: (pdf) => {
                const blob = new Blob([pdf],
                    {type: pdf.type});
                const dateFrom = (new Date(this.dateFromControl.value!)).toISOString().split('T')[0].split('-').reverse().join('-');
                const dateTo = (new Date(this.dateToControl.value!)).toISOString().split('T')[0].split('-').reverse().join('-');
                const fileName = `Reporte Ejecutivo ${dateFrom} - ${dateTo}.pdf`;
                if (!this.isMobile) {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = fileName;
                    document.body.appendChild(a);
                    a.click();
                    URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                } else {
                    const reader = new FileReader();
                    reader.onloadend = async () => {
                        const base64Data = reader.result as string;
                        const savedFile = await Filesystem.writeFile({
                            path: fileName, data: base64Data, directory: Directory.Documents,
                        });
                        const fileOpenerOptions = {
                            filePath: savedFile.uri, contentType: 'application/pdf', openWithDefault: true,
                        };
                        await FileOpener.open(fileOpenerOptions);
                    };
                    reader.readAsDataURL(blob);
                }
                this.isLoading = false;
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }


    public onClear() {
        this.generatedReport = false;
        this.executiveReport = null;
        this.dateFromControl.setValue('');
        this.dateToControl.setValue('');
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
