import {Component, OnInit, ViewChild} from '@angular/core';
import {ProfileService} from "../../../../core/services/profile.service";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {jwtDecode} from "jwt-decode";
import {Router} from "@angular/router";
import {FormControl, Validators} from "@angular/forms";
import {FileUpload} from "primeng/fileupload";
import {ProfileDto} from "../../models/profile.dto";
import {AuthService} from "../../../../core/services/auth.service";
import {MessageService} from "primeng/api";

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.scss',
    providers: [MessageService],
})
export class ProfileComponent implements OnInit {

    visibleChangePassword = false

    oldPasswordControl = new FormControl('',
        [Validators.required, Validators.minLength(8)]);
    passwordControl = new FormControl('',
        [Validators.required, Validators.minLength(8)]);
    confirmPasswordControl = new FormControl('',
        [Validators.required, Validators.minLength(8)]);


    profilePictureUrl: string = 'assets/layout/images/avatar.png';
    backupProfilePictureUrl: string = 'assets/layout/images/avatar.png';

    roles: string[] = [];
    groups: string[] = [];

    email = '';

    firstNameControl = new FormControl('',
        [Validators.required]);
    lastNameControl = new FormControl('',
        [Validators.required]);
    phoneControl = new FormControl('');
    descriptionControl = new FormControl('');

    decoded: JwtPayload | undefined

    changeProfilePicture = false;

    profile: ProfileDto | null = null;

    @ViewChild('fileUpload') fileUpload!: FileUpload;

    constructor(
        private profileService: ProfileService,
        private authService: AuthService,
        private router: Router,
        private messageService: MessageService
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            this.decoded = jwtDecode<JwtPayload>(token!);
        }
        this.roles = this.decoded?.roles || [];
        this.groups = this.decoded?.groups || [];
        this.email = this.decoded?.email || '';
    }

    ngOnInit() {
        this.getProfileInfo();
        this.getProfilePictureUrl();
    }

    public getProfilePictureUrl() {
        this.profileService.getProfilePicture().subscribe({
            next: (data) => {
                this.profilePictureUrl = URL.createObjectURL(data);
                this.backupProfilePictureUrl = URL.createObjectURL(data);
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public getProfileInfo() {
        this.profileService.getProfile().subscribe({
            next: (data) => {
                this.profile = data.data;
                this.firstNameControl.setValue(this.profile?.firstName ?? '');
                this.lastNameControl.setValue(this.profile?.lastName ?? '');
                this.phoneControl.setValue(this.profile?.phone ?? '');
                this.descriptionControl.setValue(this.profile?.description ?? '');
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public onSelect(event: any) {
        this.changeProfilePicture = true
        this.profilePictureUrl = URL.createObjectURL(event.files[0]);
    }

    public onCancelSelect() {
        this.changeProfilePicture = false;
        this.fileUpload.clear();
        this.profilePictureUrl = this.backupProfilePictureUrl;
    }

    public onSave() {
        this.onUpload();
        this.profileService.updateProfile(this.firstNameControl.value!,
            this.lastNameControl.value!,
            this.phoneControl.value!,
            this.descriptionControl.value!).subscribe({
            next: (data) => {
                this.refreshToken();
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Perfil actualizado'});
                setTimeout(() => {
                        this.router.navigate(['/']).then(r => window.location.reload());
                    },
                    500);
            }, error: (error) => {
                console.log(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        })
    }

    public onUpload() {
        if (this.changeProfilePicture) {
            this.profileService.uploadProfilePicture(this.fileUpload.files[0]).subscribe({
                next: (data) => {
                    this.backupProfilePictureUrl = this.profilePictureUrl;
                    this.onCancelSelect();
                }, error: (error) => {
                    console.log(error);
                }
            });
        }
    }

    public refreshToken() {
        const refreshToken = localStorage.getItem('refreshToken') || '';
        this.authService.refreshToken(refreshToken).subscribe({
            next: (data) => {
                localStorage.setItem('token',
                    data.data!.token);
                localStorage.setItem('refreshToken',
                    data.data!.refreshToken);
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    onSubmit() {
        this.profileService.changePassword(this.oldPasswordControl.value!,
            this.passwordControl.value!,
            this.confirmPasswordControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Contraseña actualizada'});
                this.visibleChangePassword = false;
            }, error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
                console.log(error);
            }
        });
    }

}
