import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {TaskDto} from "../../models/task.dto";
import {ConfirmationService, MenuItem, MessageService, SelectItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {FileDto} from "../../../../core/models/file.dto";
import {TaskService} from "../../../../core/services/task.service";
import {UtilService} from "../../../../core/services/util.service";
import {FileService} from "../../../../core/services/file.service";
import {Router} from "@angular/router";
import {FormControl} from "@angular/forms";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {TaskCommentService} from "../../../../core/services/task-comment.service";
import {TaskHistoryDto} from "../../models/task-history.dto";
import {Directory, Filesystem} from "@capacitor/filesystem";
import {FileOpener} from "@capacitor-community/file-opener";

@Component({
    selector: 'app-view-task',
    templateUrl: './view-task.component.html',
    styleUrl: './view-task.component.scss',
    providers: [MessageService, ConfirmationService,],
})
export class ViewTaskComponent implements OnInit {

    @Input() sidebarVisible: boolean = false;
    @Input() taskId: number = 0;
    @Input() isOwner: boolean = false;

    @Output() sidebarVisibleChange = new EventEmitter<boolean>();
    @ViewChildren('buttonEl') buttonEl!: QueryList<ElementRef>;
    @ViewChildren('buttonOp') buttonOp!: QueryList<ElementRef>;

    taskName = '';
    taskDescription = '';
    taskDeadline = '';
    taskFeedback = '';
    taskRating = 0;

    taskRatingControl = new FormControl('');
    taskFeedbackControl = new FormControl('');

    selectedPriority: any = {value: ''};

    priorityItems: SelectItem[] = [];

    selectedStatus: any = {value: ''};

    statusItems: SelectItem[] = [];

    focusedComment: boolean = false;

    taskHistory: TaskHistoryDto[] = [];


    users: UserDto[] = [];

    today: Date = new Date();

    userItems: SelectItem[] = [];
    selectedAssignees: any[] = [];

    baseUrl: string = `${environment.API_URL}/api/v1/users`;

    filesBaselUrl: string = `${environment.API_URL}/api/v1/files`;

    imgLoaded: { [key: string]: boolean } = {};

    loading: boolean = false;

    task: TaskDto | null = null;

    defaultDisplay: string = 'none';

    userId: number = 0;
    userFullName: string = '';

    newCommentControl = new FormControl('');

    editCommentControl = new FormControl('');

    menuItems: MenuItem[] = [];

    selectedCommentId: number = 0;

    uploadedFiles: FileDto[] = [];

    showNewCommentAttachment: boolean = false;

    newCommentFiles: any[] = [];

    editComment: boolean = false;

    editCommentFiles: any[] = [];

    editUploadedFiles: FileDto[] = [];

    showEditCommentAttachment: boolean = false;

    visibleAddFeedback: boolean = false;

    isMobile: boolean = false;


    constructor(
        private taskService: TaskService,
        private messageService: MessageService,
        private utilService: UtilService,
        private fileService: FileService,
        private router: Router,
        private taskCommentService: TaskCommentService,
        private confirmationService: ConfirmationService
    ) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        this.defaultDisplay = this.utilService.checkIfMobile() ? 'true' : 'none';
        this.isMobile = this.utilService.checkIfMobile();
        this.filesBaselUrl = this.utilService.getApiUrl(this.filesBaselUrl);
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            this.userId = decoded.userId;
            this.userFullName = `${decoded.givenName} ${decoded.familyName}`;
        }

        this.menuItems = [{label: 'Editar', icon: 'pi pi-pencil', command: () => this.onCommentEdit()},
            {label: 'Eliminar', icon: 'pi pi-trash', command: () => this.onCommentDelete()}];
    }

    ngOnInit() {
        this.priorityItems = [{label: 'Nivel 1', value: 1}, {label: 'Nivel 2', value: 2}, {label: 'Nivel 3', value: 3},
            {label: 'Nivel 4', value: 4}, {label: 'Nivel 5', value: 5}, {label: 'Nivel 6', value: 6},
            {label: 'Nivel 7', value: 7}, {label: 'Nivel 8', value: 8}, {label: 'Nivel 9', value: 9},
            {label: 'Nivel 10', value: 10},];
    }

    public onSidebarShow() {
        this.getStatuses()
        this.getTask()
        this.getHistory()
    }

    public getHistory() {
        if (this.isOwner) {
            this.taskService.getTaskHistory(this.taskId).subscribe({
                next: (data) => {
                    this.taskHistory = data.data!;
                }, error: (error) => {
                    console.log(error);
                }
            });
        }
    }

    public getStatuses() {
        this.taskService.getStatuses().subscribe({
            next: (data) => {
                this.statusItems = data.data!.map(status => {
                    return {label: status.taskStatusName, value: status.taskStatusId}
                });
            }, error: (error) => {
                console.log(error);
            }
        });
    }

    public getTask() {
        this.loading = true;
        this.taskService.getTask(this.taskId).subscribe({
            next: (data) => {
                this.task = data.data;
                this.taskName = this.task!.taskName;
                this.taskDescription = this.task!.taskDescription;
                this.taskFeedback = this.task!.feedback;
                this.taskRating = this.task!.rating;
                let datePart = new Date(this.task!.taskDeadline).toLocaleDateString('en-GB');
                let timePart = new Date(this.task!.taskDeadline).toLocaleTimeString('en-GB',
                    {
                        hour: '2-digit', minute: '2-digit'
                    });
                this.taskDeadline = `${datePart} ${timePart}`;
                this.selectedPriority = this.task!.taskPriority;
                this.selectedStatus = this.task!.taskStatus.taskStatusId;
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
        this.taskName = '';
        this.taskDescription = '';
        this.taskDeadline = '';
        this.selectedPriority = {value: ''};
        this.selectedAssignees = [];
        this.userItems = [];
        this.statusItems = [];
        this.loading = false;

        this.taskRating = 0;
        this.taskFeedback = '';

        this.taskRatingControl.setValue('');
        this.taskFeedbackControl.setValue('');

        this.onCommentCancel();
        this.onEditCommentCancel();
    }

    public onFileMouseOver(file: any) {
        if (this.isMobile) {
            return;
        }
        this.buttonOp.toArray().forEach(el => {
            Number(el.nativeElement.id) === file.fileId ? el.nativeElement.style.display = 'flex' : null;
        })
    }

    public onFileMouseLeave(file: any) {
        if (this.isMobile) {
            return;
        }
        this.buttonOp.toArray().forEach(el => {
            Number(el.nativeElement.id) === file.fileId ? el.nativeElement.style.display = 'none' : null;
        })
    }

    public downloadFile(file: FileDto) {
        this.fileService.getFile(file.fileId).subscribe({
            next: (data) => {
                const blob = new Blob([data],
                    {type: file.contentType});
                if (!this.isMobile) {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = file.filename;
                    document.body.appendChild(a);
                    a.click();
                    URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                } else {
                    const reader = new FileReader();
                    reader.onloadend = async () => {
                        const base64Data = reader.result as string;
                        const savedFile = await Filesystem.writeFile({
                            path: file.filename,
                            data: base64Data,
                            directory: Directory.Documents,
                        });
                        const fileOpenerOptions = {
                            filePath: savedFile.uri,
                            contentType: file.contentType,
                            openWithDefault: true,
                        };
                        await FileOpener.open(fileOpenerOptions);

                    };
                    reader.readAsDataURL(blob);
                }
            }, error: (error) => {
                console.log(error);
            }
        });
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

    public onStatusChange(event: any) {
        const taskStatus = this.statusItems.find(status => status.value === event.value)!;
        this.confirmationService.confirm({
            key: 'confirmStatusChange',
            message: `¿Estás seguro de que deseas cambiar el estado de la tarea a ${taskStatus.label}?`,
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                if (this.task!.taskStatus.taskStatusId !== 3 && event.value !== 3) {
                    this.updateTaskStatus(taskStatus);
                } else {
                    this.visibleAddFeedback = true;
                }
            },
            reject: () => {
                this.selectedStatus = this.task!.taskStatus.taskStatusId;
            }
        });
    }

    public updateTaskStatus(
        taskStatus: any,
        addFeedback: boolean = false
    ) {
        this.taskService.updateTaskStatus(this.taskId,
            taskStatus.value,
            taskStatus.label!).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Estado de la tarea actualizado correctamente'
                });
                if (addFeedback) {
                    this.createTaskFeedback();
                } else {
                    setTimeout(() => {
                            let currentRoute = this.router.url;
                            this.router.navigateByUrl('/',
                                {skipLocationChange: true}).then(() => {
                                this.router.navigate([currentRoute]).then(r => console.log('Task status updated'));
                            });
                        },
                        500);
                    this.onClose();
                }
            }, error: (error) => {
                console.log(error);
            }
        });

    }

    public onComment(event: any | null = null) {
        this.loading = true;
        if (this.newCommentFiles.length == 0) {
            this.saveTaskComment();
            return;
        }
        this.newCommentFiles.forEach(file => {
            this.fileService.uploadFile(file).subscribe({
                next: (data) => {
                    this.uploadedFiles.push(data.data!);
                    if (this.uploadedFiles.length == this.newCommentFiles.length) {
                        this.saveTaskComment();
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
    }

    public saveTaskComment() {
        this.taskCommentService.createTaskComment(this.taskId,
            this.newCommentControl.value!,
            this.uploadedFiles.map(file => file.fileId)).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Comentario creado correctamente'
                });
                setTimeout(() => {
                        let currentRoute = this.router.url;
                        this.router.navigateByUrl('/',
                            {skipLocationChange: true}).then(() => {
                            this.router.navigate([currentRoute]).then(r => console.log('Task comment created'));
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

    public onCommentCancel() {
        this.newCommentControl.setValue('');
        this.focusedComment = false;
        this.showNewCommentAttachment = false;
        this.newCommentFiles = [];
        this.uploadedFiles = [];
    }

    public onEditCommentCancel() {
        this.editCommentControl.setValue('');
        this.showEditCommentAttachment = false;
        this.editComment = false;
        this.selectedCommentId = 0;
        this.editCommentFiles = [];
        this.editUploadedFiles = [];
    }

    public onCommentEdit() {
        this.editComment = true;
        const comment = this.task!.taskComments.find(comment => comment.taskCommentId === this.selectedCommentId)!;
        comment.taskCommentFiles.forEach(file => {
            this.editCommentFiles.push({
                id: file.fileId,
                name: file.filename,
                size: file.fileSize,
                type: file.contentType,
                objectURL: (file.contentType.includes('image') ? `${this.filesBaselUrl}/${file.fileId}/thumbnail` : null)
            });
        });
        if (this.editCommentFiles.length > 0) {
            this.showEditCommentAttachment = true;
        }
        this.editCommentControl.setValue(comment.comment);
    }

    public onCommentDelete() {
        this.confirmationService.confirm({
            key: 'confirmDeleteComment',
            message: '¿Estás seguro de que deseas eliminar este comentario? Esta acción no se puede deshacer.',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Sí',
            rejectLabel: 'No',
            accept: () => {
                this.deleteTaskComment(this.selectedCommentId);
            },
        });
    }

    public deleteTaskComment(commentId: number) {
        this.taskCommentService.deleteTaskComment(commentId).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Comentario eliminado correctamente'
                });
                setTimeout(() => {
                        let currentRoute = this.router.url;
                        this.router.navigateByUrl('/',
                            {skipLocationChange: true}).then(() => {
                            this.router.navigate([currentRoute]).then(r => console.log('Task comment deleted'));
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

    public onButtonClick(event: any) {
        this.selectedCommentId = event;
        this.editComment = false;
        this.editCommentFiles = [];
    }

    public onCommentFocus() {
        this.focusedComment = true;
    }

    public onFileUpload() {
        this.showNewCommentAttachment = true;
    }

    public onEditFileUpload() {
        this.showEditCommentAttachment = true;
    }

    public onUpload(event: any) {
        for (let file of event.files) {
            if (!this.newCommentFiles.some(f => f.name === file.name)) {
                this.newCommentFiles.push(file);
            }
        }
    }

    public removeFile(file: any) {
        this.newCommentFiles = this.newCommentFiles.filter(f => f !== file);
    }

    public onNewFileMouseOver(file: any) {
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

    public onNewFileMouseLeave(file: any) {
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

    public onEditFileMouseOver(file: any) {
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

    public onEditFileMouseLeave(file: any) {
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

    public onEditUpload(event: any) {
        for (let file of event.files) {
            if (!this.editCommentFiles.some(f => f.name === file.name)) {
                this.editCommentFiles.push(file);
            }
        }
    }

    public removeEditFile(file: any) {
        this.editCommentFiles = this.editCommentFiles.filter(f => f !== file);
    }

    public downloadEditFile(file: any) {
        console.log(file)
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
                reader.readAsDataURL(file);
            }
        } else {
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
                }, error: (error) => {
                    console.log(error);
                }
            });
        }
    }

    public onEditComment(event: any | null = null) {
        this.loading = true;
        if (this.editCommentFiles.length == 0) {
            this.updateTaskComment();
            return;
        }
        this.editCommentFiles.forEach(file => {
            if (file.id === undefined) {
                this.fileService.uploadFile(file).subscribe({
                    next: (data) => {
                        this.editUploadedFiles.push(data.data!);
                        if (this.editUploadedFiles.length == this.editCommentFiles.length) {
                            this.updateTaskComment();
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
                this.editUploadedFiles.push({
                    fileId: file.id,
                    filename: file.name,
                    contentType: file.type,
                    fileSize: file.size
                });
                if (this.editUploadedFiles.length == this.editCommentFiles.length) {
                    this.updateTaskComment();
                }
            }
        });
    }

    public updateTaskComment() {
        this.taskCommentService.updateTaskComment(this.selectedCommentId,
            this.editCommentControl.value!,
            this.editUploadedFiles.map(file => file.fileId)).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Comentario actualizado correctamente'
                });
                setTimeout(() => {
                        let currentRoute = this.router.url;
                        this.router.navigateByUrl('/',
                            {skipLocationChange: true}).then(() => {
                            this.router.navigate([currentRoute]).then(r => console.log('Task comment updated'));
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

    public onAddFeedbackCancel() {
        this.visibleAddFeedback = false
        this.selectedStatus = this.task!.taskStatus.taskStatusId;
    }

    public onAddFeedback() {
        this.updateTaskStatus({value: 3, label: 'FINALIZADO'},
            true);
    }

    public createTaskFeedback() {
        this.taskService.createTaskFeedback(this.taskId,
            Number(this.taskRatingControl.value),
            this.taskFeedbackControl.value!).subscribe({
            next: (data) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Retroalimentación de la tarea creada correctamente'
                });

                this.visibleAddFeedback = false;
                setTimeout(() => {
                        let currentRoute = this.router.url;
                        this.router.navigateByUrl('/',
                            {skipLocationChange: true}).then(() => {
                            this.router.navigate([currentRoute]).then(r => console.log('Task feedback created'));
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

