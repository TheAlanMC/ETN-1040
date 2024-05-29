import {Component, OnInit} from '@angular/core';
import {environment} from "../../../../../environments/environment";
import {ConfirmationService, MessageService, SelectItem} from "primeng/api";
import {ProjectDto} from "../../models/project.dto";
import {ActivatedRoute, Router} from "@angular/router";
import {ProjectService} from "../../../../core/services/project.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {FormControl, Validators} from "@angular/forms";

@Component({
    selector: 'app-project-detail',
    templateUrl: './project-detail.component.html',
    styleUrl: './project-detail.component.scss',
    providers: [MessageService, ConfirmationService],
})
export class ProjectDetailComponent implements OnInit {

    isLoading: boolean = false;

    projectId: number = 0;

    editorModules = {
        toolbar: [
            [
                {
                    'header': [
                        1,
                        2,
                        false
                    ]
                }
            ],
            [
                'bold',
                'italic',
                'underline'
            ],
            [
                {'color': []},
                {'background': []}
            ],
            [
                {'list': 'ordered'},
                {'list': 'bullet'},
                {'align': []}
            ],
            [
                {indent: '-1'},
                {indent: '+1'}
            ],
        ]
    };

    baseUrl: string = `${environment.API_URL}/api/v1/users`;
    imgLoaded: { [key: string]: boolean } = {};
    userId: number = 0;
    userItems: SelectItem[] = [];
    // selectedProjectManagers: any[] = [];
    selectedModerators: any[] = [];
    selectedMembers: any[] = [];

    project: ProjectDto | null = null;

    dateFrom: string = '';
    dateTo: string = '';

    projectProgress: number = 0;

    visibleAddCloseMessage: boolean = false;

    projectCloseMessageControl = new FormControl('',
        [Validators.required]);

    daysOfDifference: number = 0;

    constructor(
        private activatedRoute: ActivatedRoute,
        private projectService: ProjectService,
        private router: Router,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            this.userId = decoded.userId;
        }
    }

    ngOnInit() {
        this.activatedRoute.parent?.params.subscribe(params => {
            this.projectId = params['id'];
            this.getProjectInfo();
        });
    }

    public getProjectInfo() {
        this.projectService.getProject(this.projectId).subscribe({
            next: (data) => {
                this.project = data.data!;
                this.dateFrom = new Date(data.data!.projectDateFrom).toLocaleDateString('en-GB')
                this.dateTo = new Date(data.data!.projectDateTo).toLocaleDateString('en-GB')
                this.selectedMembers = data.data!.projectMembers.map(member => {
                    this.fetchUserImage(member.userId);
                    return {
                        label: `${member.firstName} ${member.lastName}`,
                        labelSecondary: member.email,
                        value: member.userId,
                        disabled: (member.userId === this.userId)
                    }
                });
                this.selectedModerators = data.data!.projectModerators.map(moderator => {
                    this.fetchUserImage(moderator.userId);
                    return {
                        label: `${moderator.firstName} ${moderator.lastName}`,
                        labelSecondary: moderator.email,
                        value: moderator.userId,
                        disabled: (moderator.userId === this.userId)
                    }
                });
                if (this.project.projectEndDate != null) {
                    const dateTo = new Date(this.project.projectDateTo);
                    const dateEnd = new Date(this.project.projectEndDate);
                    const difference = dateEnd.getTime() - dateTo.getTime();
                    this.daysOfDifference = Math.ceil(difference / (1000 * 3600 * 24));
                    this.daysOfDifference = this.daysOfDifference < 0 ? 0 : this.daysOfDifference;
                }
                this.projectProgress = (data.data!.finishedTasks / data.data!.totalTasks) * 100;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public fetchUserImage(userId: number) {
        const img = new Image();
        img.src = this.baseUrl + '/' + userId + '/profile-picture/thumbnail';
        img.onload = () => this.imgLoaded[userId] = true;
        img.onerror = () => this.imgLoaded[userId] = false;
    }

    public onBack() {
        this.router.navigate(['/projects']).then(r => console.log('Navigate to projects'));
    }

    public onCloseProject() {
        this.confirmationService.confirm({
            key: 'confirmCloseProject',
            message: '¿Estás seguro de que deseas cerrar este proyecto? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.visibleAddCloseMessage = true;
            }
        });
    }

    public onAddCloseMessageCancel() {
        this.visibleAddCloseMessage = false;
        this.projectCloseMessageControl.setValue('');
    }

    public onAddCloseMessage() {
        this.isLoading = true;
        this.projectService.closeProject(this.projectId,
            this.projectCloseMessageControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Proyecto cerrado'});
                setTimeout(() => {
                        this.router.navigate(['/projects']).then(r => console.log('Redirect to projects page'));
                        this.isLoading = false;
                    },
                    500);
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

}
