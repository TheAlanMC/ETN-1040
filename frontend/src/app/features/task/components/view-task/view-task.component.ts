import { Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {TaskDto} from "../../models/task.dto";
import {MessageService, SelectItem} from "primeng/api";
import {UserDto} from "../../../user/models/user.dto";
import {environment} from "../../../../../environments/environment";
import {FileDto} from "../../../../core/models/file.dto";
import {TaskService} from "../../../../core/services/task.service";
import {UtilService} from "../../../../core/services/util.service";
import {FileService} from "../../../../core/services/file.service";

@Component({
    selector: 'app-view-task', templateUrl: './view-task.component.html', styleUrl: './view-task.component.scss'
})
export class ViewTaskComponent implements OnInit {

    @Input() sidebarVisible: boolean = false;
    @Input() taskId: number = 0;

    @Output() sidebarVisibleChange = new EventEmitter<boolean>();
    @ViewChildren('buttonOp') buttonOp!: QueryList<ElementRef>;

    taskName = '';
    taskDescription = '';
    taskDeadline = '';

    selectedPriority: any = {value: ''};

    priorityItems: SelectItem[] = [];

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



    constructor(private taskService: TaskService, private messageService: MessageService, private utilService: UtilService, private fileService: FileService) {
        this.baseUrl = this.utilService.getApiUrl(this.baseUrl);
        this.defaultDisplay = this.utilService.checkIfMobile() ? 'true' : 'none';
        this.filesBaselUrl = this.utilService.getApiUrl(this.filesBaselUrl);
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
                this.taskName = this.task!.taskName;
                this.taskDescription = this.task!.taskDescription;
                let datePart = new Date(this.task!.taskDeadline).toLocaleDateString('en-GB');
                let timePart = new Date(this.task!.taskDeadline).toLocaleTimeString('en-GB', {
                    hour: '2-digit', minute: '2-digit'
                });
                this.taskDeadline = `${datePart} ${timePart}`;
                this.selectedPriority = this.task!.taskPriority;
                this.selectedAssignees = this.task!.taskAssignees.map(assignee => {
                    const img = new Image();
                    img.src = this.baseUrl + '/' + assignee.userId + '/profile-picture/thumbnail';
                    img.onload = () => this.imgLoaded[assignee.userId] = true;
                    img.onerror = () => this.imgLoaded[assignee.userId] = false;
                    return {
                        label: `${assignee.firstName} ${assignee.lastName}`, labelSecondary: assignee.email, value: assignee.userId,
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
        this.loading = false;
    }

    public onFileMouseOver(file: any) {
        this.buttonOp.toArray().forEach(el => {
            Number(el.nativeElement.id) === file.fileId ? el.nativeElement.style.display = 'flex' : null;
        })
    }

    public onFileMouseLeave(file: any) {
        this.buttonOp.toArray().forEach(el => {
            Number(el.nativeElement.id) === file.fileId ? el.nativeElement.style.display = 'none' : null;
        })
    }

    public downloadFile(file: FileDto) {
       this.fileService.getFile(file.fileId).subscribe({
            next: (data) => {
                const blob = new Blob([data], {type: file.contentType});
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = file.filename;
                document.body.appendChild(a);
                a.click();
                URL.revokeObjectURL(url);
                document.body.removeChild(a);
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
        return fileName.length > maxLength ? `${fileName.substring(0, maxLength)}...${fileName.split('.').pop()}` : fileName;
    }


}

