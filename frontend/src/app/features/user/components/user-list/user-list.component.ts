import {Component, OnInit} from '@angular/core';
import {UserDto} from "../../models/user.dto";
import {UserService} from "../../../../core/services/user.service";
import {Router} from "@angular/router";
import {ResponseDto} from "../../../../core/models/response.dto";
import {PageDto} from "../../../../core/models/page.dto";
import {debounceTime, Subject} from "rxjs";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {jwtDecode} from "jwt-decode";
import {ConfirmationService, MessageService} from "primeng/api";
import {environment} from "../../../../../environments/environment";
import {UtilService} from "../../../../core/services/util.service";

@Component({
    selector: 'app-user-list',
    templateUrl: './user-list.component.html',
    styleUrl: './user-list.component.scss',
    providers: [
        ConfirmationService,
        MessageService,
    ],
})
export class UserListComponent implements OnInit {

    userId: number = 0;

    users: UserDto[] = [];

    // Pagination variables
    sortBy: string = 'userId';
    sortType: string = 'asc';
    page: number = 0;
    size: number = 10;
    keyword: string = '';

    totalElements: number = 0;

    canAddUser: boolean = false;
    canEditUser: boolean = false;

    isLoading: boolean = true;

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};

    private searchSubject = new Subject<string>();


    constructor(
        private userService: UserService,
        private router: Router,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private utilService: UtilService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.roles.includes('CREAR USUARIOS')) {
                this.canAddUser = true;
            }
            if (decoded.roles.includes('EDITAR USUARIOS')) {
                this.canEditUser = true;
            }
            this.userId = decoded.userId;
        }
    }

    ngOnInit() {
        this.getData();
        this.searchSubject.pipe(debounceTime(500)).subscribe(() => {
            this.getData()
        });
    }

    public navigateToCreateUser() {
        this.router.navigate(['/users/create']).then(r => console.log('Navigate to create user'));
    }

    public navigateToViewUser(userId: number) {
        this.router.navigate(['/users/view/' + userId]).then(r => console.log('Navigate to view user'));
    }

    public navigateToEditUser(userId: number) {
        this.router.navigate(['/users/edit/' + userId]).then(r => console.log('Navigate to edit user'));
    }

    public onPageChange(event: any) {
        const first = event.first;
        const rows = event.rows;
        this.page = Math.floor(first / rows);
        this.size = rows;
        this.getData();
    }

    public onSortChange(event: any) {
        this.sortBy = event.field;
        this.sortType = (event.order == 1) ? 'asc' : 'desc';
        this.getData();
    }

    public getData() {
        this.isLoading = true;
        this.userService.getUsers(this.sortBy,
            this.sortType,
            this.page,
            this.size,
            this.keyword).subscribe({
            next: (data: ResponseDto<PageDto<UserDto>>) => {
                this.users = data.data!.content;
                this.totalElements = data.data!.page.totalElements;
                this.isLoading = false
                // Filter the users to remove the current user
                // this.users = this.users.filter(user => user.userId !== this.userId);
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onSearch(event: any) {
        this.keyword = event.target.value;
        this.searchSubject.next(this.keyword);
    }


    public onDeleteUser(userId: number) {
        this.confirmationService.confirm({
            key: 'confirmDeleteUser',
            message: '¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.deleteUser(userId);
            },
        });
    }

    public deleteUser(userId: number): void {
        this.userService.deleteUser(userId).subscribe({
            next: (data) => {
                this.getData();
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Usuario eliminado correctamente'
                });
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }
}
