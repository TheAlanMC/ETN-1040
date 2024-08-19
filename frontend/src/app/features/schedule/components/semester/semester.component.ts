import {Component, OnInit} from '@angular/core';
import {ConfirmationService, MessageService, SelectItem} from "primeng/api";
import {FormControl, Validators} from "@angular/forms";
import {environment} from "../../../../../environments/environment";
import {UserDto} from "../../../user/models/user.dto";
import {UserService} from "../../../../core/services/user.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {SemesterService} from "../../../../core/services/semester.service";
import {SemesterDto} from "../../models/semester.dto";

@Component({
    selector: 'app-semester',
    templateUrl: './semester.component.html',
    styleUrl: './semester.component.scss',
    providers: [MessageService, ConfirmationService,],
})
export class SemesterComponent implements OnInit {

    isLoading: boolean = false;

    selectedSemesterId: number = 0;

    selectedSemester: SelectItem = {value: ''};

    semesterItems: SelectItem[] = [];

    assistantItems: SelectItem[] = [];

    sourceAssistants: any[] = [{name: 'Seleccione un usuario', code: ''}];
    targetAssistants: any[] = [{name: 'Seleccione un usuario', code: ''}];

    canCreateSemester: boolean = false;
    canEditSemester: boolean = false;

    semesters: SemesterDto[] = [];

    visibleAddSemester = false;
    visibleEditSemester = false;

    semesterNameControl = new FormControl('',
        [Validators.required]);
    semesterDateFromControl = new FormControl('',
        [Validators.required]);
    semesterDateToControl = new FormControl('',
        [Validators.required]);

    imgLoaded: { [key: string]: boolean } = {};
    users: UserDto[] = [];
    code: string | undefined;

    originalDateFrom: string = '';
    originalDateTo: string = '';

    constructor(
        private userService: UserService,
        private confirmationService: ConfirmationService,
        private semesterService: SemesterService,
        private messageService: MessageService,
    ) {
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            this.canCreateSemester = decoded.permissions.includes('CREAR HORARIOS');
            this.canEditSemester = decoded.permissions.includes('EDITAR HORARIOS');
        }
    }

    ngOnInit() {
        this.getSemesters();
        this.getAllUsers();
    }

    public getAllUsers() {
        this.userService.getAllUsers().subscribe({
            next: (data) => {
                this.users = data.data!;
                this.assistantItems = data.data!.map(user => {
                    return {
                        label: `${user.firstName} ${user.lastName}`,
                        labelSecondary: user.email,
                        value: user.userId
                    }
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public getSemesters() {
        this.semesterService.getSemesters().subscribe({
            next: (data) => {
                this.semesters = data.data!;
                this.semesterItems = data.data!.map(semester => {
                    return {
                        label: semester.semesterName,
                        value: semester.semesterId
                    }
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onSelectSemester(event: any) {
        if (event.value == null) {
            return
        }
        this.semesterService.getSemesterAssistants(event.value).subscribe({
            next: (data) => {
                this.selectedSemesterId = event.value;
                this.targetAssistants = data.data!.map(assistant => {
                    return {
                        name: assistant.assistant.firstName + ' ' + assistant.assistant.lastName,
                        email: assistant.assistant.email,
                        code: assistant.assistant.userId,
                    }
                });
                this.sourceAssistants = this.users.filter(user => data.data!.findIndex(assistant => assistant.assistant.userId === user.userId) === -1).map(user => {
                    return {
                        name: user.firstName + ' ' + user.lastName,
                        email: user.email,
                        code: user.userId,
                    }
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onClearSemester() {
        this.selectedSemesterId = 0;
        this.selectedSemester = {value: ''};
        this.sourceAssistants = [{name: 'Seleccione un usuario', code: ''}];
        this.targetAssistants = [{name: 'Seleccione un usuario', code: ''}];
    }

    public saveSemesterAssistants() {
        this.isLoading = true;
        this.semesterService.addAssistantToSemester(this.selectedSemesterId,
            this.targetAssistants.map(assistant => assistant.code)).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Auxiliares asignados correctamente'
                });
                this.isLoading = false;
                this.selectedSemester = {value: ''};
                this.onClearSemester();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public sortSourceAssistants() {
        this.sourceAssistants.sort((
            a,
            b
        ) => a.code - b.code);
    }

    public sortTargetAssistants() {
        this.targetAssistants.sort((
            a,
            b
        ) => a.code - b.code);
    }

    public onDeleteSemester() {
        this.confirmationService.confirm({
            key: 'confirmDeleteSemester',
            message: '¿Estás seguro de que deseas eliminar este semestre? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.deleteSemester();
            },
        });
    }

    public deleteSemester() {
        this.semesterService.deleteSemester(this.selectedSemesterId).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Semestre eliminado correctamente'
                });
                this.selectedSemester = {value: ''};
                this.onClearSemester();
                this.getSemesters();
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onAddSemester() {
        this.visibleAddSemester = true;
        this.semesterNameControl.setValue('');
        this.semesterDateFromControl.setValue('');
        this.semesterDateToControl.setValue('');
    }

    public onEditSemester() {
        this.visibleEditSemester = true;
        this.semesterNameControl.setValue(this.semesters.find(semester => semester.semesterId === this.selectedSemesterId)!.semesterName);
        const dateFrom = this.semesters.find(semester => semester.semesterId === this.selectedSemesterId)!.semesterDateFrom.split('-');
        const dateTo = this.semesters.find(semester => semester.semesterId === this.selectedSemesterId)!.semesterDateTo.split('-');
        this.originalDateFrom = `${dateFrom[2]}/${dateFrom[1]}/${dateFrom[0]}`;
        this.originalDateTo = `${dateTo[2]}/${dateTo[1]}/${dateTo[0]}`;
        this.semesterDateFromControl.setValue(this.originalDateFrom);
        this.semesterDateToControl.setValue(this.originalDateTo);
    }

    public onSaveSemester() {
        this.isLoading = true;
        const dateFrom = new Date(this.semesterDateFromControl.value!).toISOString().split('T')[0];
        const dateTo = new Date(this.semesterDateToControl.value!).toISOString().split('T')[0];
        this.semesterService.createSemester(this.semesterNameControl.value!,
            dateFrom,
            dateTo
        ).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Semestre creado correctamente'
                });
                this.isLoading = false;
                this.visibleAddSemester = false;
                this.getSemesters();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onUpdateSemester() {
        this.isLoading = true;
        const oldDateFrom = this.originalDateFrom.split('/');
        const oldDateTo = this.originalDateTo.split('/');
        const dateFrom = (this.originalDateFrom == this.semesterDateFromControl.value!) ? `${oldDateFrom[2]}-${oldDateFrom[1]}-${oldDateFrom[0]}` : new Date(this.semesterDateFromControl.value!).toISOString().split('T')[0];
        const dateTo = (this.originalDateTo == this.semesterDateToControl.value!) ? `${oldDateTo[2]}-${oldDateTo[1]}-${oldDateTo[0]}` : new Date(this.semesterDateToControl.value!).toISOString().split('T')[0];
        this.semesterService.updateSemester(this.selectedSemesterId,
            this.semesterNameControl.value!,
            dateFrom,
            dateTo
        ).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Semestre actualizado correctamente'
                });
                this.isLoading = false;
                this.visibleEditSemester = false;
                this.getSemesters();
                this.onClearSemester();
            }, error: (error) => {
                console.error(error);
                this.isLoading = false;
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }
}
