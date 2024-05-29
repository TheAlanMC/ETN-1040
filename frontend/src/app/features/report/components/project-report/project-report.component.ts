import {Component, OnInit} from '@angular/core';
import {FileDto} from "../../../../core/models/file.dto";
import {FormControl, Validators} from "@angular/forms";
import {ReportService} from "../../../../core/services/report.service";
import {FileService} from "../../../../core/services/file.service";
import {MessageService, SelectItem} from "primeng/api";
import {PdfGenerationService} from "../../../../core/services/pdf-generation.service";
import {UtilService} from "../../../../core/services/util.service";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capacitor-community/file-opener";
import {ProjectReportDto} from "../../models/project-report.dto";
import {
    ProjectReportMemberDto,
    ProjectReportModeratorDto,
    ProjectReportOwnerDto,
    ProjectReportStatusDto
} from "../../models/project-report-filter.dto";

@Component({
    selector: 'app-project-report',
    templateUrl: './project-report.component.html',
    styleUrl: './project-report.component.scss',
    providers: [MessageService],
})
export class ProjectReportComponent implements OnInit {

    isLoading: boolean = false;

    projects: ProjectReportDto [] = [];

    file: FileDto | null = null;

    dateFromControl = new FormControl('',
        [Validators.required]);
    dateToControl = new FormControl('',
        [Validators.required]);

    // Pagination variables
    sortBy: string = 'projectId';
    sortType: string = 'desc';
    page: number = 0;
    size: number = 10;

    totalElements: number = 0;

    statuses: ProjectReportStatusDto[] = [];
    statusItems: SelectItem[] = [];
    selectedStatuses: any[] = [];

    owners: ProjectReportOwnerDto[] = [];
    ownerItems: SelectItem[] = [];
    selectedOwners: any[] = [];

    moderators: ProjectReportModeratorDto[] = [];
    moderatorItems: SelectItem[] = [];
    selectedModerators: any[] = [];

    members: ProjectReportMemberDto[] = [];
    memberItems: SelectItem[] = [];
    selectedMembers: any[] = [];

    isDataLoading: boolean = true;

    generatedReport: boolean = false;

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

    public getProjectFilers() {
        this.isDataLoading = true;
        this.reportService.getProjectFilters(
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
        ).subscribe({
            next: (data) => {
                this.statuses = data.data!.statuses;
                this.statusItems = this.statuses.map((status) => {
                    return {label: status.statusName, value: status.statusId};
                });
                this.selectedStatuses = this.statusItems;

                this.owners = data.data!.projectOwners;
                this.ownerItems = this.owners.map((owner) => {
                    return {label: `${owner.firstName} ${owner.lastName}`, value: owner.userId};
                });
                this.selectedOwners = this.ownerItems;

                this.moderators = data.data!.projectModerators;
                this.moderatorItems = this.moderators.map((moderator) => {
                    return {label: `${moderator.firstName} ${moderator.lastName}`, value: moderator.userId};
                });
                this.selectedModerators = this.moderatorItems;

                this.members = data.data!.projectMembers;
                this.memberItems = this.members.map((member) => {
                    return {label: `${member.firstName} ${member.lastName}`, value: member.userId};
                });
                this.selectedMembers = this.memberItems;

            }, error: (error) => {
                console.error(error);
                this.isDataLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public getData() {
        this.isDataLoading = true;
        this.reportService.getProjectReport(
            this.sortBy,
            this.sortType,
            this.page,
            this.size,
            (new Date(this.dateFromControl.value!)).toISOString(),
            (new Date(this.dateToControl.value!)).toISOString(),
            this.selectedOwners.map((owner) => owner.value),
            this.selectedModerators.map((moderator) => moderator.value),
            this.selectedMembers.map((member) => member.value),
            this.selectedStatuses.map((status) => status.label)
        ).subscribe({
            next: (data) => {
                this.projects = data.data!.content;
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
        if (this.projects.length == 0) return;
        this.sortBy = event.field;
        this.sortType = (event.order == 1) ? 'asc' : 'desc';
        this.getData();
    }

    public onStatusChange(event: any) {
        this.isDataLoading = true;
        this.selectedStatuses = event.value;
        this.getData();
    }

    public onOwnerChange(event: any) {
        this.isDataLoading = true;
        this.selectedOwners = event.value;
        this.getData();
    }

    public onModeratorChange(event: any) {
        this.isDataLoading = true;
        this.selectedModerators = event.value;
        this.getData();
    }

    public onMemberChange(event: any) {
        this.isDataLoading = true;
        this.selectedMembers = event.value;
        this.getData();
    }

    public onSync() {
        this.isDataLoading = true;
        this.getProjectFilers();
        this.getData();
    }

    public onExportPdf() {
        this.isLoading = true;
        this.pdfGenerationService.generatePdf('executiveReportContent',
            'Reporte de Proyectos.pdf').subscribe(
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
            'PROYECTO',
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
        this.projects = [];
        this.dateFromControl.setValue('');
        this.dateToControl.setValue('');
    }

}
