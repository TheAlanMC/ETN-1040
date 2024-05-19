import {Component, OnInit} from '@angular/core';
import {environment} from "../../../../../environments/environment";
import {SelectItem} from "primeng/api";
import {ProjectDto} from "../../models/project.dto";
import {ActivatedRoute, Router} from "@angular/router";
import {UtilService} from "../../../../core/services/util.service";
import {ProjectService} from "../../../../core/services/project.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";

@Component({
    selector: 'app-project-detail',
    templateUrl: './project-detail.component.html',
    styleUrl: './project-detail.component.scss',
})
export class ProjectDetailComponent implements OnInit {
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

    constructor(
        private activatedRoute: ActivatedRoute,
        private utilService: UtilService,
        private projectService: ProjectService,
        private router: Router
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
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
                this.dateFrom = new Date(data.data!.dateFrom).toLocaleDateString('en-GB')
                this.dateTo = new Date(data.data!.dateTo).toLocaleDateString('en-GB')
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
            }, error: (error) => {
                console.log(error);
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
}
