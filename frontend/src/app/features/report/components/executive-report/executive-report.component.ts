import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {ReportService} from "../../../../core/services/report.service";
import {FileService} from "../../../../core/services/file.service";
import {MessageService} from "primeng/api";
import {ExecutiveReportDto} from "../../models/executive-report.dto";
import {ProjectReportDto} from "../../models/project-report.dto";

@Component({
    selector: 'app-executive-report',
    templateUrl: './executive-report.component.html',
    styleUrl: './executive-report.component.scss',
    providers: [MessageService],
})
export class ExecutiveReportComponent implements OnInit {

    executiveReport: ExecutiveReportDto | null = null;


    dateFromControl = new FormControl('',
        [Validators.required]);
    dateToControl = new FormControl('',
        [Validators.required]);

    totalElements: number = 0;

    isDataLoading: boolean = true;

    generatedReport: boolean = false;

    expandedRows = {};

    constructor(
        private reportService: ReportService,
        private fileService: FileService,
        private messageService: MessageService
    ) {
    }

    ngOnInit() {
        this.isDataLoading = false;
    }


    public getData() {
        this.isDataLoading = true;
        this.reportService.getExecutiveReport((new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),).subscribe({
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
        console.log('Exporting PDF');
    }

    public onClear() {
        this.generatedReport = false;
        this.executiveReport = null;
        this.dateFromControl.reset();
        this.dateToControl.reset();
    }

    expandAll() {
        this.expandedRows = this.executiveReport?.projectReports.reduce((acc: any, project:ProjectReportDto) => (acc[project.projectId] = true) && acc, {});
    }

    collapseAll() {
        this.expandedRows = {};
    }
}
