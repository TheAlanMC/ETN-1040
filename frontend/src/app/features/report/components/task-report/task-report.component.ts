import {Component, OnInit} from '@angular/core';
import {FileDto} from "../../../../core/models/file.dto";
import {FormControl, Validators} from "@angular/forms";
import {MessageService, SelectItem} from "primeng/api";
import {ReportService} from "../../../../core/services/report.service";
import {UtilService} from "../../../../core/services/util.service";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capacitor-community/file-opener";
import {TaskReportDto} from "../../models/task-report.dto";
import {
    TaskReportAssigneeDto,
    TaskReportPriorityDto,
    TaskReportProjectDto,
    TaskReportStatusDto
} from "../../models/task-report-filter.dto";

@Component({
    selector: 'app-task-report',
    templateUrl: './task-report.component.html',
    styleUrl: './task-report.component.scss',
    providers: [MessageService]
})
export class TaskReportComponent implements OnInit {

    isLoading: boolean = false;

    tasks: TaskReportDto [] = [];

    file: FileDto | null = null;

    dateFromControl = new FormControl('',
        [Validators.required]);
    dateToControl = new FormControl('',
        [Validators.required]);

    // Pagination variables
    sortBy: string = 'taskId';
    sortType: string = 'asc';
    page: number = 0;
    size: number = 10;

    totalElements: number = 0;

    statuses: TaskReportStatusDto[] = [];
    statusItems: SelectItem[] = [];
    selectedStatuses: any[] = [];

    priorities: TaskReportPriorityDto[] = [];
    priorityItems: SelectItem[] = [];
    selectedPriorities: any[] = [];

    projects: TaskReportProjectDto[] = [];
    projectItems: SelectItem[] = [];
    selectedProjects: any[] = [];

    assignees: TaskReportAssigneeDto[] = [];
    assigneeItems: SelectItem[] = [];
    selectedAssignees: any[] = [];


    isDataLoading: boolean = true;

    generatedReport: boolean = false;

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

    public getTaskFilers() {
        this.isDataLoading = true;
        this.reportService.getTaskFilters((new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
        ).subscribe({
            next: (data) => {
                this.projects = data.data!.projects;
                this.projectItems = this.projects.map((project) => {
                    return {label: project.projectName, value: project.projectId};
                });
                this.selectedProjects = this.projectItems;

                this.statuses = data.data!.statuses;
                this.statusItems = this.statuses.map((status) => {
                    return {label: status.statusName, value: status.statusId};
                });
                this.selectedStatuses = this.statusItems;

                this.priorities = data.data!.priorities;
                this.priorityItems = this.priorities.map((priority) => {
                    return {label: priority.priorityName, value: priority.priorityId};
                });
                this.selectedPriorities = this.priorityItems;

                this.assignees = data.data!.taskAssignees;
                this.assigneeItems = this.assignees.map((assignee) => {
                    return {label: assignee.firstName + ' ' + assignee.lastName, value: assignee.userId};
                });
                this.selectedAssignees = this.assigneeItems;

            }, error: (error) => {
                console.error(error);
                this.isDataLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public getData() {
        this.isDataLoading = true;
        this.reportService.getTaskReport(this.sortBy,
            this.sortType,
            this.page,
            this.size,
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
            this.selectedProjects.map((project) => project.value),
            this.selectedAssignees.map((assignee) => assignee.value),
            this.selectedStatuses.map((status) => status.label),
            this.selectedPriorities.map((priority) => priority.label),
        ).subscribe({
            next: (data) => {
                this.tasks = data.data!.content;
                this.isDataLoading = false;
                this.generatedReport = true;
                this.totalElements = data.data!.page.totalElements;
            }, error: (error) => {
                console.error(error);
                this.isDataLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onPageChange(event: any) {
        const first = event.first;
        const rows = event.rows;
        this.page = Math.floor(first / rows);
        this.size = rows;
        this.getData();
    }

    public onSortChange(event: any) {
        if (this.tasks.length == 0) return;
        this.sortBy = event.field;
        this.sortType = (event.order == 1) ? 'asc' : 'desc';
        this.getData();
    }

    public onStatusChange(event: any) {
        this.isDataLoading = true;
        this.selectedStatuses = event.value;
        this.getData();
    }

    public onPriorityChange(event: any) {
        this.isDataLoading = true;
        this.selectedPriorities = event.value;
        this.getData();
    }

    public onProjectChange(event: any) {
        this.isDataLoading = true;
        this.selectedProjects = event.value;
        this.getData();
    }

    public onAssigneeChange(event: any) {
        this.isDataLoading = true;
        this.selectedAssignees = event.value;
        this.getData();
    }

    public onSync() {
        this.isDataLoading = true;
        this.getTaskFilers();
        this.getData();
    }

    public onExportPdf() {
        this.isLoading = true;
        this.reportService.getTaskReportPdf(
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
            this.projectItems.map((project) => project.value),
            this.assigneeItems.map((assignee) => assignee.value),
            this.statusItems.map((status) => status.label!),
            this.priorityItems.map((priority) => priority.label!),
        ).subscribe({
            next: (pdf) => {
                const blob = new Blob([pdf],
                    {type: pdf.type});
                const dateFrom = (new Date(this.dateFromControl.value!)).toISOString().split('T')[0].split('-').reverse().join('-');
                const dateTo = (new Date(this.dateToControl.value!)).toISOString().split('T')[0].split('-').reverse().join('-');
                const fileName = `Reporte de Tareas ${dateFrom} - ${dateTo}.pdf`;
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
        this.projects = [];
        this.dateFromControl.setValue('');
        this.dateToControl.setValue('');
    }

}
