import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {MessageService, SelectItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {FileDto} from "../../../../core/models/file.dto";
import {UserService} from "../../../../core/services/user.service";
import {TaskService} from "../../../../core/services/task.service";
import {UtilService} from "../../../../core/services/util.service";
import {FileService} from "../../../../core/services/file.service";
import {Router} from "@angular/router";
import {TaskDto} from "../../models/task.dto";

@Component({
    selector: 'app-edit-task',
    templateUrl: './edit-task.component.html',
    styleUrl: './edit-task.component.scss',
    providers: [MessageService]
})
export class EditTaskComponent implements OnInit {

    @Input() sidebarVisible: boolean = false;
    @Input() task: TaskDto | null = null;

    @Output() sidebarVisibleChange = new EventEmitter<boolean>();
    @ViewChildren('buttonEl') buttonEl!: QueryList<ElementRef>;
    @ViewChildren('buttonOp') buttonOp!: QueryList<ElementRef>;


    taskNameControl = new FormControl('', [Validators.required]);
    taskDescriptionControl = new FormControl('');
    taskDeadlineControl = new FormControl('', [Validators.required]);
    selectedPriority: any = {value: ''};

    priorityItems: SelectItem[] = [];

    users: UserDto[] = [];

    today: Date = new Date();

    userItems: SelectItem[] = [];
    selectedAssignees: any[] = [];

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};

    files: any[] = [];

    uploadedFiles: FileDto[] = [];

    newUploadedFiles: FileDto[] = [];

    loading: boolean = false;

    objectURLs: any[] = [];


    constructor(private userService: UserService, private taskService: TaskService, private messageService: MessageService, private utilService: UtilService, private fileService: FileService, private router: Router) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
    }

    ngOnInit() {
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
        this.getAllUsers();
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


    public onSidebarShow() {
        // Set the form values
        this.taskNameControl.setValue(this.task!.taskName);
        this.taskDescriptionControl.setValue(this.task!.taskDescription);
        let datePart = new Date(this.task!.taskDeadline).toLocaleDateString('en-GB');
        let timePart = new Date(this.task!.taskDeadline).toLocaleTimeString('en-GB', {
            hour: '2-digit', minute: '2-digit'
        });
        this.taskDeadlineControl.setValue(`${datePart} ${timePart}`);
        this.selectedPriority = this.task!.taskPriority;
        this.selectedAssignees = this.task!.taskAssigneeIds.map(assignee => {
            const user = this.users.find(u => u.userId === assignee);
            return {
                label: `${user!.firstName} ${user!.lastName}`, labelSecondary: user!.email, value: user!.userId,
            }
        });
        this.userItems = this.users.filter(user => this.task!.project.projectMemberIds.includes(user.userId)).map(user => {
            // Pre-fetch the image
            const img = new Image();
            img.src = this.baseUrl + '/' + user.userId + '/profile-picture/thumbnail';
            img.onload = () => this.imgLoaded[user.userId] = true;
            img.onerror = () => this.imgLoaded[user.userId] = false;
            return {
                label: `${user.firstName} ${user.lastName}`, labelSecondary: user.email, value: user.userId,
            }
        });
        this.task!.taskFileIds.forEach(fileId => {
            this.fileService.getFile(fileId).subscribe({
                next: (data) => {
                    let fileName = '';
                    const contentDisposition = data.headers.get('Content-Disposition');
                    if (contentDisposition) {
                        const fileNameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                        const matches = fileNameRegex.exec(contentDisposition);
                        if (matches != null && matches[1]) {
                            fileName = matches[1].replace(/['"]/g, '');
                        }
                    }
                    this.files.push(new File([data.body!], fileName, {type: data.headers.get('Content-Type')!}));
                    this.uploadedFiles.push({
                        fileId: fileId, filename: fileName, contentType: data.headers.get('Content-Type')!
                    });
                }, error: (error) => {
                    console.log(error);
                }
            });
        });
    }

    public getObjectURL(file: any): string {
        if (this.objectURLs[file.name]) {
            return this.objectURLs[file.name];
        }
        const objectURL = URL.createObjectURL(file);
        this.objectURLs[file.name] = objectURL;
        return objectURL;
    }


    public onClose() {
        this.sidebarVisibleChange.emit(false);
        this.sidebarVisible = false;
        this.newUploadedFiles = [];
        this.objectURLs = [];
        this.taskNameControl.reset();
        this.taskDescriptionControl.reset();
        this.taskDeadlineControl.reset();
        this.selectedAssignees = [];
        this.selectedPriority = {value: ''};
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
            this.updateTask();
            return;
        }
        // Check if the file is already uploaded
        this.files.forEach(file => {
            let fileExists = this.uploadedFiles.find(f => f.filename === file.name);
            if (fileExists) {
                this.newUploadedFiles.push(fileExists);
                if (this.newUploadedFiles.length == this.files.length) {
                    this.updateTask();
                }
            } else {
                this.fileService.uploadFile(file).subscribe({
                    next: (data) => {
                        this.newUploadedFiles.push(data.data!);
                        if (this.newUploadedFiles.length == this.files.length) {
                            this.updateTask();
                        }
                    }, error: (error) => {
                        console.log(error);
                        this.loading = false;
                        this.newUploadedFiles = [];
                        this.messageService.add({
                            severity: 'error', summary: 'Error', detail: error.error.message
                        });
                    }
                });
            }
        });
    }

    public updateTask() {
        let taskDeadlineDate = new Date(this.task!.taskDeadline);
        this.taskService.updateTask(this.task!.taskId, this.taskNameControl.value!, this.taskDescriptionControl.value!, (`${taskDeadlineDate.toLocaleDateString('en-GB')} ${taskDeadlineDate.toLocaleTimeString('en-GB', {
            hour: '2-digit', minute: '2-digit'
        })}` === this.taskDeadlineControl.value) ? taskDeadlineDate.toISOString() : this.taskDeadlineControl.value!, this.selectedPriority, this.selectedAssignees.map(assignee => assignee.value), this.newUploadedFiles.map(file => file.fileId)).subscribe({
            next: (data) => {
                this.loading = false;
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Tarea actualizada correctamente'
                });
                setTimeout(() => {
                    let currentRoute = this.router.url;
                    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
                        this.router.navigate([currentRoute]).then(r => console.log('Task updated successfully'));
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
