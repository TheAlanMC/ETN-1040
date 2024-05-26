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
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capacitor-community/file-opener";
import {TaskPriorityDto} from "../../models/task-priority.dto";

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
    taskDueDateControl = new FormControl('',
        [Validators.required]);
    selectedPriority: any = {value: ''};

    priorities: TaskPriorityDto[] = [];

    priorityItems: SelectItem[] = [];

    users: UserDto[] = [];

    today: Date = new Date();

    userItems: SelectItem[] = [];
    selectedAssignees: any[] = [];

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    imgLoaded: { [key: string]: boolean } = {};

    files: any[] = [];

    uploadedFiles: FileDto[] = [];

    isLoading: boolean = false;

    objectURLs: any[] = [];

    task: TaskDto | null = null;

    filesBaselUrl: string = `${environment.API_URL}/api/v1/files`;

    defaultDisplay: string = 'none';

    isMobile: boolean = false;

    downloadingFileId: number = 0;

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
        this.isMobile = this.utilService.checkIfMobile();
    }

    ngOnInit() {
        this.getAllPriorities();
    }

    public onSidebarShow() {
        this.getTask()
    }

    public getAllPriorities() {
        this.taskService.getPriorities().subscribe({
            next: (data) => {
                this.priorities = data.data!;
                this.priorityItems = this.priorities.map(priority => {
                    return {
                        label: priority.taskPriorityName, value: priority.taskPriorityId
                    }
                });
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public getTask() {
        this.taskService.getTask(this.taskId).subscribe({
            next: (data) => {
                this.task = data.data;
                this.taskNameControl.setValue(this.task!.taskName);
                this.taskDescriptionControl.setValue(this.task!.taskDescription);
                let datePart = new Date(this.task!.taskDueDate).toLocaleDateString('en-GB');
                let timePart = new Date(this.task!.taskDueDate).toLocaleTimeString('en-GB',
                    {
                        hour: '2-digit', minute: '2-digit'
                    });
                this.taskDueDateControl.setValue(`${datePart} ${timePart}`);
                this.selectedPriority = this.task!.taskPriority.taskPriorityId;
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
                        name: file.fileName,
                        size: file.fileSize,
                        type: file.contentType,
                        objectURL: (file.contentType.includes('image') ? `${this.filesBaselUrl}/${file.fileId}/thumbnail` : null)
                    });
                });
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
        this.taskDueDateControl.reset();
        this.selectedPriority = {value: ''};
        this.selectedAssignees = [];
        this.userItems = [];
        this.files = [];
        this.uploadedFiles = [];
        this.objectURLs = [];
        this.isLoading = false;
    }

    public onUpload(event: any) {
        for (let file of event.files) {
            if (!this.files.some(f => f.name === file.name)) {
                this.files.push(file);
            }
        }
    }

    public onFileMouseOver(file: any) {
        if (this.isMobile) {
            return;
        }
        this.buttonEl.toArray().forEach(el => {
            el.nativeElement.id === file.name ? el.nativeElement.style.display = 'flex' : null;
        })
        this.buttonOp.toArray().forEach(el => {
            el.nativeElement.id === file.name ? el.nativeElement.style.display = 'flex' : null;
        })
    }

    public onFileMouseLeave(file: any) {
        if (this.isMobile) {
            return;
        }
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
            if (!this.isMobile) {
                const url = URL.createObjectURL(file);
                const a = document.createElement('a');
                a.href = url;
                a.download = file.name;
                document.body.appendChild(a);
                a.click();
                URL.revokeObjectURL(url);
                document.body.removeChild(a);
            } else {
                const reader = new FileReader();
                reader.onloadend = async () => {
                    const base64Data = reader.result as string;
                    const savedFile = await Filesystem.writeFile({
                        path: file.name, data: base64Data, directory: Directory.Documents,
                    });
                    const fileOpenerOptions = {
                        filePath: savedFile.uri, contentType: file.type, openWithDefault: true,
                    };
                    await FileOpener.open(fileOpenerOptions);

                };
                reader.readAsDataURL(file);
            }
        } else {
            this.isLoading = true;
            this.downloadingFileId = file.id;
            this.fileService.getFile(file.id).subscribe({
                next: (data) => {
                    const blob = new Blob([data],
                        {type: file.type});
                    if (!this.isMobile) {
                        const url = URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.href = url;
                        a.download = file.name
                        document.body.appendChild(a);
                        a.click();
                        URL.revokeObjectURL(url);
                        document.body.removeChild(a);
                    } else {
                        const reader = new FileReader();
                        reader.onloadend = async () => {
                            const base64Data = reader.result as string;
                            const savedFile = await Filesystem.writeFile({
                                path: file.name,
                                data: base64Data,
                                directory: Directory.Documents,
                            });
                            const fileOpenerOptions = {
                                filePath: savedFile.uri,
                                contentType: file.type,
                                openWithDefault: true,
                            };
                            await FileOpener.open(fileOpenerOptions);

                        };
                        reader.readAsDataURL(blob);
                    }
                    this.isLoading = false;
                    this.downloadingFileId = 0;
                }, error: (error) => {
                    this.isLoading = false;
                    this.downloadingFileId = 0;
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
        this.isLoading = true;
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
                        this.isLoading = false;
                        this.messageService.add({
                            severity: 'error', summary: 'Error', detail: error.error.message
                        });
                    }
                });
            } else {
                this.uploadedFiles.push({
                    fileId: file.id, fileName: file.name, contentType: file.type, fileSize: file.size
                });
                if (this.uploadedFiles.length == this.files.length) {
                    this.updateTask();
                }
            }
        });
    }

    public updateTask() {
        let taskDueDateDate = new Date(this.task!.taskDueDate);
        this.taskService.updateTask(
            this.task!.taskId,
            this.task!.project.projectId,
            this.taskNameControl.value!,
            this.taskDescriptionControl.value!,
            (`${taskDueDateDate.toLocaleDateString('en-GB')} ${taskDueDateDate.toLocaleTimeString('en-GB',
                {
                    hour: '2-digit', minute: '2-digit'
                })}` === this.taskDueDateControl.value) ? taskDueDateDate.toISOString() : this.taskDueDateControl.value!,
            this.selectedPriority,
            this.selectedAssignees.map(assignee => assignee.value),
            this.uploadedFiles.map(file => file.fileId)).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success', summary: 'Éxito', detail: 'Tarea actualizada correctamente'
                });
                setTimeout(() => {
                        let currentRoute = this.router.url;
                        this.router.navigateByUrl('/',
                            {skipLocationChange: true}).then(() => {
                            this.router.navigate([currentRoute]).then(r => console.log('Task updated successfully'));
                            this.isLoading = false;
                        });
                    },
                    500);
                this.onClose();
            }, error: (error) => {
                console.log(error);
                this.uploadedFiles = [];
                this.isLoading = false;
                this.messageService.add({
                    severity: 'error', summary: 'Error', detail: error.error.message
                });
            }
        });
    }
}
