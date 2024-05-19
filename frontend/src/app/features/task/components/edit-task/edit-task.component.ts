import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {MessageService, SelectItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {FileDto} from "../../../../core/models/file.dto";
import {TaskService} from "../../../../core/services/task.service";
import {UtilService} from "../../../../core/services/util.service";
import {FileService} from "../../../../core/services/file.service";
import {Router} from "@angular/router";
import {TaskDto} from "../../models/task.dto";

@Component({
    selector: 'app-edit-task',
    templateUrl: './edit-task.component.html',
    styleUrl: './edit-task.component.scss',
    providers: [MessageService],
})
export class EditTaskComponent implements OnInit {

    @Input() sidebarVisible: boolean = false;
    @Input() taskId: number = 0;

    @Output() sidebarVisibleChange = new EventEmitter<boolean>();
    @ViewChildren('buttonEl') buttonEl!: QueryList<ElementRef>;
    @ViewChildren('buttonOp') buttonOp!: QueryList<ElementRef>;


    taskNameControl = new FormControl('',
        [Validators.required]);
    taskDescriptionControl = new FormControl('');
    taskDeadlineControl = new FormControl('',
        [Validators.required]);
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

    loading: boolean = false;

    objectURLs: any[] = [];

    task: TaskDto | null = null;

    filesBaselUrl: string = `${environment.API_URL}/api/v1/files`;

    defaultDisplay: string = 'none';


    constructor(
        private taskService: TaskService,
        private messageService: MessageService,
        private utilService: UtilService,
        private fileService: FileService,
        private router: Router
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        this.filesBaselUrl = this.utilService.getApiUrl(this.filesBaselUrl);
        this.defaultDisplay = this.utilService.checkIfMobile() ? 'true' : 'none';
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
    }

    public onSidebarShow() {
        this.getTask()
    }

    public getTask() {
        this.loading = true;
        this.taskService.getTask(this.taskId).subscribe({
            next: (data) => {
                this.task = data.data;
                this.taskNameControl.setValue(this.task!.taskName);
                this.taskDescriptionControl.setValue(this.task!.taskDescription);
                let datePart = new Date(this.task!.taskDeadline).toLocaleDateString('en-GB');
                let timePart = new Date(this.task!.taskDeadline).toLocaleTimeString('en-GB',
                    {
                        hour: '2-digit', minute: '2-digit'
                    });
                this.taskDeadlineControl.setValue(`${datePart} ${timePart}`);
                this.selectedPriority = this.task!.taskPriority;
                this.selectedAssignees = this.task!.taskAssignees.map(assignee => {
                    const img = new Image();
                    img.src = this.baseUrl + '/' + assignee.userId + '/profile-picture/thumbnail';
                    img.onload = () => this.imgLoaded[assignee.userId] = true;
                    img.onerror = () => this.imgLoaded[assignee.userId] = false;
                    return {
                        label: `${assignee.firstName} ${assignee.lastName}`,
                        labelSecondary: assignee.email,
                        value: assignee.userId,
                    }
                });
                this.userItems = this.task!.project.projectMembers.map(user => {
                    const img = new Image();
                    img.src = this.baseUrl + '/' + user.userId + '/profile-picture/thumbnail';
                    img.onload = () => this.imgLoaded[user.userId] = true;
                    img.onerror = () => this.imgLoaded[user.userId] = false;
                    return {
                        label: `${user.firstName} ${user.lastName}`, labelSecondary: user.email, value: user.userId,
                    }
                });

                this.task?.taskFiles.forEach(file => {
                    this.files.push({
                        id: file.fileId,
                        name: file.filename,
                        size: file.fileSize,
                        type: file.contentType,
                        objectURL: (file.contentType.includes('image') ? `${this.filesBaselUrl}/${file.fileId}/thumbnail` : null)
                    });
                });
                this.loading = false;
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public onClose() {
        this.sidebarVisibleChange.emit(false);
        this.sidebarVisible = false;
        this.task = null;
        this.taskNameControl.reset();
        this.taskDescriptionControl.reset();
        this.taskDeadlineControl.reset();
        this.selectedPriority = {value: ''};
        this.selectedAssignees = [];
        this.userItems = [];

        this.files = [];
        this.uploadedFiles = [];
        this.objectURLs = [];
        this.loading = false;
    }

    public onUpload(event: any) {
        for (let file of event.files) {
            if (!this.files.some(f => f.name === file.name)) {
                this.files.push(file);
            }
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
        if (file.id === undefined) {
            const url = URL.createObjectURL(file);
            const a = document.createElement('a');
            a.href = url;
            a.download = file.name;
            document.body.appendChild(a);
            a.click();
            URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } else {
            this.fileService.getFile(file.id).subscribe({
                next: (data) => {
                    const blob = new Blob([data],
                        {type: file.type});
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = file.name;
                    document.body.appendChild(a);
                    a.click();
                    URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                }, error: (error) => {
                    console.log(error);
                }
            });
        }
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
        return fileName.length > maxLength ? `${fileName.substring(0,
            maxLength)}...${fileName.split('.').pop()}` : fileName;
    }

    public onSave() {
        this.loading = true;
        if (this.files.length == 0) {
            this.updateTask();
            return;
        }
        this.files.forEach(file => {
            if (file.id === undefined) {
                this.fileService.uploadFile(file).subscribe({
                    next: (data) => {
                        this.uploadedFiles.push(data.data!);
                        if (this.uploadedFiles.length == this.files.length) {
                            this.updateTask();
                        }
                    }, error: (error) => {
                        console.log(error);
                        this.loading = false;
                        this.messageService.add({
                            severity: 'error', summary: 'Error', detail: error.error.message
                        });
                    }
                });
            } else {
                this.uploadedFiles.push({
                    fileId: file.id,
                    filename: file.name,
                    contentType: file.type,
                    fileSize: file.size
                });
                if (this.uploadedFiles.length == this.files.length) {
                    this.updateTask();
                }
            }
        });
    }

    public updateTask() {
        let taskDeadlineDate = new Date(this.task!.taskDeadline);
        this.taskService.updateTask(this.task!.taskId,
            this.taskNameControl.value!,
            this.taskDescriptionControl.value!,
            (`${taskDeadlineDate.toLocaleDateString('en-GB')} ${taskDeadlineDate.toLocaleTimeString('en-GB',
                {
                    hour: '2-digit', minute: '2-digit'
                })}` === this.taskDeadlineControl.value) ? taskDeadlineDate.toISOString() : this.taskDeadlineControl.value!,
            this.selectedPriority,
            this.selectedAssignees.map(assignee => assignee.value),
            this.uploadedFiles.map(file => file.fileId)).subscribe({
            next: (data) => {
                this.loading = false;
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Tarea actualizada correctamente'
                });
                setTimeout(() => {
                        let currentRoute = this.router.url;
                        this.router.navigateByUrl('/',
                            {skipLocationChange: true}).then(() => {
                            this.router.navigate([currentRoute]).then(r => console.log('Task updated successfully'));
                        });
                    },
                    500);
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
