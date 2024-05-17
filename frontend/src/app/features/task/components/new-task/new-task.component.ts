import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {ProjectDto} from "../../../project/models/project.dto";
import {UserService} from "../../../../core/services/user.service";
import {ProjectService} from "../../../../core/services/project.service";
import {TaskService} from "../../../../core/services/task.service";
import {MessageService, SelectItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {UtilService} from '../../../../core/services/util.service';
import {FileService} from "../../../../core/services/file.service";
import {FileDto} from "../../../../core/models/file.dto";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-new-task',
    templateUrl: './new-task.component.html',
    styleUrl: './new-task.component.scss',
    providers: [MessageService]
})
export class NewTaskComponent implements OnInit {

    @Input() sidebarVisible: boolean = false;
    @Input() projectId: number = 0;
    @Input() deadline: Date | null = null
    @Output() sidebarVisibleChange = new EventEmitter<boolean>();
    @ViewChildren('buttonEl') buttonEl!: QueryList<ElementRef>;
    @ViewChildren('buttonOp') buttonOp!: QueryList<ElementRef>;


    taskNameControl = new FormControl('', [Validators.required]);
    taskDescriptionControl = new FormControl('');
    taskDeadlineControl = new FormControl('', [Validators.required]);
    selectedPriority: any = {value: ''};

    priorityItems: SelectItem[] = [];

    projects: ProjectDto[] = [];

    projectItems: SelectItem[] = [];

    users: UserDto[] = [];

    today: Date = new Date();

    userItems: SelectItem[] = [];
    selectedAssignees: any[] = [];

    selectedProject: any = null;

    project: ProjectDto | null = null;

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};

    files: any[] = [];

    uploadedFiles: FileDto[] = [];

    loading: boolean = false;

    showProjectDropdown: boolean = false;

    constructor(private userService: UserService, private projectService: ProjectService, private taskService: TaskService, private messageService: MessageService, private utilService: UtilService, private fileService: FileService, private router: Router, private route: ActivatedRoute) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    ngOnInit() {
        this.showProjectDropdown = this.projectId == 0;

        this.priorityItems = [
            {label: 'Nivel 1', value: 1},
            {label: 'Nivel 2', value: 2},
            {label: 'Nivel 3', value: 3},
            {label: 'Nivel 4', value: 4},
            {label: 'Nivel 5', value: 5},
            {label: 'Nivel 6', value: 6},
            {label: 'Nivel 7', value: 7},
            {label: 'Nivel 8', value: 8},
            {label: 'Nivel 9', value: 9},
            {label: 'Nivel 10', value: 10},
        ];
        this.getAllProjects();
        this.getAllUsers();
    }


    public getAllProjects() {
        this.projectService.getAllProjects().subscribe({
            next: (data) => {
                this.projects = data.data!;
                this.projectItems = this.projects.map(project => {
                    return {label: project.projectName, value: project.projectId};
                });
                this.selectedProject = this.projectItems.find(project => project.value == this.projectId)?.value;
                this.onProjectChange(null);
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public getAllUsers() {
        this.userService.getAllUsers().subscribe({
            next: (data) => {
                this.users = data.data!;
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public onProjectChange(event: any) {
        this.selectedAssignees = [];
        this.project = this.projects.find(project => project.projectId == this.selectedProject) ?? null;
        if (this.project != null) {
            this.taskNameControl.enable();
            this.taskDescriptionControl.enable();
            this.taskDeadlineControl.enable();
            this.userItems = this.users.filter(user => this.project!.projectMemberIds.includes(user.userId)).map(user => {
                // Pre-fetch the image
                const img = new Image();
                img.src = this.baseUrl + '/' + user.userId + '/profile-picture/thumbnail';
                img.onload = () => this.imgLoaded[user.userId] = true;
                img.onerror = () => this.imgLoaded[user.userId] = false;
                return {
                    label: `${user.firstName} ${user.lastName}`, labelSecondary: user.email, value: user.userId,
                }
            });
        } else {
            this.taskNameControl.disable();
            this.taskDescriptionControl.disable();
            this.taskDeadlineControl.disable();
        }
    }

    public onSidebarShow() {
        if (this.deadline != null) {
            let datePart = new Date(this.deadline!).toLocaleDateString('en-GB');
            let timePart = new Date(this.deadline!).toLocaleTimeString('en-GB', {hour: '2-digit', minute: '2-digit'});
            this.taskDeadlineControl.setValue(`${datePart} ${timePart}`);
        }
    }


    public onClose() {
        this.sidebarVisibleChange.emit(false);
        this.sidebarVisible = false;
        this.taskNameControl.reset();
        this.taskDescriptionControl.reset();
        this.taskDeadlineControl.reset();
        this.selectedAssignees = [];
        this.selectedPriority = {value: ''};
        this.selectedProject = null;
        this.files = [];
        this.uploadedFiles = [];
        this.loading = false;
    }

    public onUpload(event: any) {
        for (let file of event.files) {
            this.files.push(file);
        }
    }

    public onFileMouseOver(file: any) {
        this.buttonEl.toArray().forEach(el => {
            el.nativeElement.id === file.name ? el.nativeElement.style.display = 'flex' : null;
        })
        this.buttonOp.toArray().forEach(el => {
            el.nativeElement.id === file.name ? el.nativeElement.style.display = 'flex' : null;
        })
    }

    public onFileMouseLeave(file: any) {
        this.buttonEl.toArray().forEach(el => {
            el.nativeElement.id === file.name ? el.nativeElement.style.display = 'none' : null;
        })
        this.buttonOp.toArray().forEach(el => {
            el.nativeElement.id === file.name ? el.nativeElement.style.display = 'none' : null;
        })
    }

    public removeFile(file: any) {
        this.files = this.files.filter(f => f !== file);
    }

    public downloadFile(file: any) {
        const url = URL.createObjectURL(file);
        const a = document.createElement('a');
        a.href = url;
        a.download = file.name;
        document.body.appendChild(a);
        a.click();
        URL.revokeObjectURL(url);
        document.body.removeChild(a);
    }

    public getFileExtension(fileName: string): string {
        return fileName.slice((fileName.lastIndexOf(".") - 1 >>> 0) + 2);
    }

    public getFileDetails(fileName: string): { color: string, extension: string, x: string } {
        const extension = this.getFileExtension(fileName).toUpperCase();
        let color = '#7e8997';
        let x = '18%';
        switch (extension.toLowerCase()) {
            case 'pdf':
                color = '#d73b41';
                x = '18%';
                break;
            case 'doc':
                color = '#2c77b1';
                x = '20%';
                break;
            case 'docx':
                color = '#2c77b1';
                x = '0%';
                break;
            case 'xls':
                color = '#54b51e';
                x = '20%';
                break;
            case 'xlsx':
                color = '#54b51e';
                x = '7%';
                break;
            case 'ppt':
                color = '#e89e00';
                x = '20%';
                break;
            case 'pptx':
                color = '#e89e00';
                x = '5%';
                break;
        }
        return {color, extension, x};
    }

    public getTruncatedFileName(fileName: string): string {
        const maxLength = 20; // Maximum number of characters to show
        return fileName.length > maxLength ? `${fileName.substring(0, maxLength)}...${fileName.split('.').pop()}` : fileName;
    }

    public onSave() {
        this.loading = true;
        if (this.files.length == 0) {
            this.saveTask();
            return;
        }
        this.files.forEach(file => {
            this.fileService.uploadFile(file).subscribe({
                next: (data) => {
                    this.uploadedFiles.push(data.data!);
                    if (this.uploadedFiles.length == this.files.length) {
                        this.saveTask();
                    }
                }, error: (error) => {
                    console.log(error);
                    this.loading = false;
                    this.messageService.add({
                        severity: 'error', summary: 'Error', detail: error.error.message
                    });
                }
            });
        });
        this.loading = true;
    }

    public saveTask() {
        this.taskService.createTask(this.selectedProject, this.taskNameControl.value!, this.taskDescriptionControl.value!, this.taskDeadlineControl.value!, this.selectedPriority, this.selectedAssignees.map(assignee => assignee.value), this.uploadedFiles.map(file => file.fileId)).subscribe({
            next: (data) => {
                this.loading = false;
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Tarea creada con éxito'
                });
                setTimeout(() => {
                    let currentRoute = this.router.url;
                    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
                        this.router.navigate([currentRoute]).then(r => console.log('Task created successfully'));
                    });
                }, 500);
                this.onClose();
            }, error: (error) => {
                console.log(error);
                this.loading = false;
                this.messageService.add({
                    severity: 'error', summary: 'Error', detail: error.error.message
                });
            }
        });
    }
}
