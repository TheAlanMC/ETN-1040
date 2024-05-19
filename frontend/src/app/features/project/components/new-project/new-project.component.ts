import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {MessageService, SelectItem} from "primeng/api";
import {environment} from "../../../../../environments/environment";
import {UserDto} from "../../../user/models/user.dto";
import {UserService} from "../../../../core/services/user.service";
import {UtilService} from "../../../../core/services/util.service";
import {Location} from '@angular/common';
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {ProjectService} from "../../../../core/services/project.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-new-project',
    templateUrl: './new-project.component.html',
    styleUrl: './new-project.component.scss',
    providers: [MessageService],
})
export class NewProjectComponent implements OnInit {

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
    projectDescriptionControl = new FormControl('');

    baseUrl: string = `${environment.API_URL}/api/v1/users`;
    imgLoaded: { [key: string]: boolean } = {};
    userId: number = 0;
    users: UserDto[] = [];
    userItems: SelectItem[] = [];
    // selectedProjectManagers: any[] = [];
    selectedModerators: any[] = [];
    selectedMembers: any[] = [];


    constructor(
        private userService: UserService,
        private messageService: MessageService,
        private utilService: UtilService,
        private location: Location,
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
        this.getAllUsers();
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

    public onCancel() {
        this.location.back();
    }

    public onSave() {
        // Convert the date to ISO format
        this.projectService.createProject(this.projectNameControl.value!,
            this.projectDescriptionControl.value!,
            this.dateFromControl.value!,
            this.dateToControl.value!,
            this.selectedMembers.map(member => member.value),
            this.selectedModerators.map(moderator => moderator.value)).subscribe({
            next: (data) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Proyecto creado'});
                setTimeout(() => {
                        this.router.navigate(['/projects']).then(r => console.log('Redirect to projects page'));
                    },
                    500);
            }, error: (error) => {
                console.log(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }
}
