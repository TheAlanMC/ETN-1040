import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {environment} from "../../../../../environments/environment";
import {UserDto} from "../../../user/models/user.dto";
import {MessageService, SelectItem} from "primeng/api";
import {UserService} from "../../../../core/services/user.service";
import {Location} from "@angular/common";
import {ProjectService} from "../../../../core/services/project.service";
import {ActivatedRoute, Router} from "@angular/router";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ProjectDto} from "../../models/project.dto";

@Component({
    selector: 'app-edit-project',
    templateUrl: './edit-project.component.html',
    styleUrl: './edit-project.component.scss',
    providers: [MessageService],
})
export class EditProjectComponent implements OnInit {

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

    projectNameControl = new FormControl('',
        [Validators.required]);
    dateFromControl = new FormControl('',
        [Validators.required]);
    dateToControl = new FormControl('',
        [Validators.required]);
    projectObjectiveControl = new FormControl('',
        [Validators.required]);

    projectDescriptionControl = new FormControl('');

    baseUrl: string = `${environment.API_URL}/api/v1/users`;
    imgLoaded: { [key: string]: boolean } = {};
    userId: number = 0;
    users: UserDto[] = [];
    userItems: SelectItem[] = [];
    // selectedProjectManagers: any[] = [];
    selectedModerators: any[] = [];
    selectedMembers: any[] = [];

    project: ProjectDto | null = null;


    constructor(
        private activatedRoute: ActivatedRoute,
        private userService: UserService,
        private messageService: MessageService,
        private location: Location,
        private projectService: ProjectService,
        private router: Router,
    ) {
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            this.userId = decoded.userId;
        }
    }

    ngOnInit() {
        this.projectId = this.activatedRoute.snapshot.params['id'];
        this.getAllUsers();
        this.getProjectInfo();
    }

    public getProjectInfo() {
        this.projectService.getProject(this.projectId).subscribe({
            next: (data) => {
                this.project = data.data!;
                this.projectNameControl.setValue(data.data!.projectName);
                this.projectDescriptionControl.setValue(data.data!.projectDescription);
                this.projectObjectiveControl.setValue(data.data!.projectObjective);
                this.dateFromControl.setValue(new Date(data.data!.projectDateFrom).toLocaleDateString('en-GB'));
                this.dateToControl.setValue(new Date(data.data!.projectDateTo).toLocaleDateString('en-GB'));
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
                console.error(error);
            }
        });
    }

    public getAllUsers() {
        this.userService.getAllUsers().subscribe({
            next: (data) => {
                this.users = data.data!;
                this.userItems = data.data!.map(user => {
                    this.fetchUserImage(user.userId);
                    return {
                        label: `${user.firstName} ${user.lastName}`,
                        labelSecondary: user.email,
                        value: user.userId,
                        disabled: (user.userId === this.userId)
                    }
                });
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

    public onCancel() {
        this.location.back();
    }

    public onSave() {
        this.isLoading = true;
        let dateFrom = new Date(this.project!.projectDateFrom)
        let dateTo = new Date(this.project!.projectDateTo)
        this.projectService.updateProject(this.projectId,
            this.projectNameControl.value!,
            this.projectDescriptionControl.value!,
            (dateFrom.toLocaleDateString('en-GB') === this.dateFromControl.value!) ? dateFrom.toISOString() : this.dateFromControl.value!,
            (dateTo.toLocaleDateString('en-GB') === this.dateToControl.value!) ? dateTo.toISOString() : this.dateToControl.value!,
            this.projectObjectiveControl.value!,
            this.selectedMembers.map(member => member.value),
            this.selectedModerators.map(moderator => moderator.value)).subscribe({
            next: (data) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Proyecto actualizado'});
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

