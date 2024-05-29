import {Component, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {DashboardService} from "../../../../core/services/dashboard.service";
import {TaskDashboardDto} from "../../models/task-dashboard.dto";
import {ProjectDashboardDto} from "../../models/project-dashboard.dto";
import {MessageService} from "primeng/api";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss',
    providers: [MessageService]
})
export class DashboardComponent implements OnInit {
    monthNames = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre",
                  "Noviembre", "Diciembre"];

    taskDashboard: TaskDashboardDto | null = null;

    projectDashboard: ProjectDashboardDto | null = null;

    dateFromControl = new FormControl();
    dateToControl = new FormControl();

    defaultDateFrom = new Date(new Date().getFullYear(),
        0,
        1,);
    defaultDateTo = new Date();

    currentDateFrom = this.defaultDateFrom;
    currentDateTo = this.defaultDateTo;

    taskBarData: any;
    taskBarOptions: any;

    taskPieData: any;
    taskPieOptions: any;

    taskLineData: any;
    taskLineOptions: any;

    projectBarData: any;
    projectBarOptions: any;

    projectLineData: any;
    projectLineOptions: any;

    constructor(
        private dashboardService: DashboardService,
        private messageService: MessageService,
    ) {
        this.dateFromControl.setValue(new Date(this.defaultDateFrom).toLocaleDateString('en-GB'));
        this.dateToControl.setValue(new Date(this.defaultDateTo).toLocaleDateString('en-GB'));
    }

    ngOnInit() {
        this.initCharts();
        this.getTaskDashboard();
        this.getProjectDashboard();
    }

    public initCharts() {
        const documentStyle = getComputedStyle(document.documentElement);
        const textColor = documentStyle.getPropertyValue('--text-color');
        const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
        const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

        this.projectBarData = {
            labels: ['Cerrado', 'Cerrado con Retraso', 'Abierto', 'Atrasado',], datasets: [{
                label: 'Proyectos',
                backgroundColor: [documentStyle.getPropertyValue('--green-400'),
                                  documentStyle.getPropertyValue('--yellow-400'),
                                  documentStyle.getPropertyValue('--blue-400'),
                                  documentStyle.getPropertyValue('--red-400'),],
                borderColor: [documentStyle.getPropertyValue('--green-300'),
                              documentStyle.getPropertyValue('--yellow-300'),
                              documentStyle.getPropertyValue('--blue-300'),
                              documentStyle.getPropertyValue('--red-300'),],
                hoverBackgroundColor: [documentStyle.getPropertyValue('--green-500'),
                                       documentStyle.getPropertyValue('--yellow-500'),
                                       documentStyle.getPropertyValue('--blue-500'),
                                       documentStyle.getPropertyValue('--red-500'),],
                data: [],
            }],
        };

        this.projectBarOptions = {
            plugins: {
                legend: {
                    labels: {
                        fontColor: textColor,
                    },
                },
            }, scales: {
                x: {
                    ticks: {
                        color: textColorSecondary, font: {
                            weight: 500,
                        },
                    }, grid: {
                        display: false, drawBorder: false,
                    },
                }, y: {
                    ticks: {
                        color: textColorSecondary,
                    }, grid: {
                        color: surfaceBorder, drawBorder: false,
                    },
                },
            },
        };

        this.projectLineData = {
            labels: [],
            datasets: [
                {
                    label: 'Cerrado',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--green-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--green-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--green-500'),
                    tension: 0.4,
                },
                {
                    label: 'Cerrado con Retraso',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--yellow-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--yellow-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--yellow-500'),
                    tension: 0.4,
                },
                {
                    label: 'Abierto',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--blue-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--blue-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--blue-500'),
                    tension: 0.4,
                },
                {
                    label: 'Atrasado',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--red-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--red-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--red-500'),
                    tension: 0.4,
                },
            ],
        };

        this.projectLineOptions = {
            plugins: {
                legend: {
                    labels: {
                        fontColor: textColor,
                    },
                },
            },
            scales: {
                x: {
                    ticks: {
                        color: textColorSecondary,
                    },
                    grid: {
                        color: surfaceBorder,
                        drawBorder: false,
                    },
                },
                y: {
                    ticks: {
                        color: textColorSecondary,
                    },
                    grid: {
                        color: surfaceBorder,
                        drawBorder: false,
                    },
                },
            },
        };


        this.taskBarData = {
            labels: ['Finalizado', 'Finalizado con Retraso', 'En Progreso', 'Pendiente', 'Atrasado',], datasets: [{
                label: 'Tareas',
                backgroundColor: [documentStyle.getPropertyValue('--green-400'),
                                  documentStyle.getPropertyValue('--yellow-400'),
                                  documentStyle.getPropertyValue('--blue-400'),
                                  documentStyle.getPropertyValue('--orange-400'),
                                  documentStyle.getPropertyValue('--red-400'),],
                borderColor: [documentStyle.getPropertyValue('--green-300'),
                              documentStyle.getPropertyValue('--yellow-300'),
                              documentStyle.getPropertyValue('--blue-300'),
                              documentStyle.getPropertyValue('--orange-300'),
                              documentStyle.getPropertyValue('--red-300'),],
                hoverBackgroundColor: [documentStyle.getPropertyValue('--green-500'),
                                       documentStyle.getPropertyValue('--yellow-500'),
                                       documentStyle.getPropertyValue('--blue-500'),
                                       documentStyle.getPropertyValue('--orange-500'),
                                       documentStyle.getPropertyValue('--red-500'),],
                data: [],
            }],
        };

        this.taskBarOptions = {
            plugins: {
                legend: {
                    labels: {
                        fontColor: textColor,
                    },
                },
            }, scales: {
                x: {
                    ticks: {
                        color: textColorSecondary, font: {
                            weight: 500,
                        },
                    }, grid: {
                        display: false, drawBorder: false,
                    },
                }, y: {
                    ticks: {
                        color: textColorSecondary,
                    }, grid: {
                        color: surfaceBorder, drawBorder: false,
                    },
                },
            },
        };

        this.taskPieData = {
            labels: ['Alta', 'Media', 'Baja',],
            datasets: [
                {
                    data: [],
                    backgroundColor: [
                        documentStyle.getPropertyValue('--red-400'),
                        documentStyle.getPropertyValue('--yellow-400'),
                        documentStyle.getPropertyValue('--green-400'),
                    ],
                    hoverBackgroundColor: [
                        documentStyle.getPropertyValue('--red-500'),
                        documentStyle.getPropertyValue('--yellow-500'),
                        documentStyle.getPropertyValue('--green-500'),
                    ],
                },
            ],
        };

        this.taskPieOptions = {
            plugins: {
                legend: {
                    labels: {
                        usePointStyle: true,
                        color: textColor,
                    },
                },
            },
        };

        this.taskLineData = {
            labels: [],
            datasets: [
                {
                    label: 'Finalizado',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--green-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--green-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--green-500'),
                    tension: 0.4,
                },
                {
                    label: 'Finalizado con Retraso',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--yellow-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--yellow-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--yellow-500'),
                    tension: 0.4,
                },
                {
                    label: 'En Progreso',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--blue-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--blue-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--blue-500'),
                    tension: 0.4,
                },
                {
                    label: 'Pendiente',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--orange-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--orange-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--orange-500'),
                    tension: 0.4,
                },
                {
                    label: 'Atrasado',
                    data: [],
                    fill: false,
                    backgroundColor:
                        documentStyle.getPropertyValue('--red-400'),
                    borderColor:
                        documentStyle.getPropertyValue('--red-300'),
                    hoverBackgroundColor:
                        documentStyle.getPropertyValue('--red-500'),
                    tension: 0.4,
                },
            ],
        };

        this.taskLineOptions = {
            plugins: {
                legend: {
                    labels: {
                        fontColor: textColor,
                    },
                },
            },
            scales: {
                x: {
                    ticks: {
                        color: textColorSecondary,
                    },
                    grid: {
                        color: surfaceBorder,
                        drawBorder: false,
                    },
                },
                y: {
                    ticks: {
                        color: textColorSecondary,
                    },
                    grid: {
                        color: surfaceBorder,
                        drawBorder: false,
                    },
                },
            },
        };
    }

    public getTaskDashboard() {
        this.dashboardService.getTaskDashboard(this.currentDateFrom.toISOString(),
            this.currentDateTo.toISOString(),).subscribe({
            next: (response) => {
                this.taskDashboard = response.data;
                this.taskBarData.datasets[0].data = [this.taskDashboard?.totalCompletedTasks,
                                                     this.taskDashboard?.totalCompletedWithDelayTasks,
                                                     this.taskDashboard?.totalInProgressTasks,
                                                     this.taskDashboard?.totalPendingTasks,
                                                     this.taskDashboard?.totalDelayedTasks,];
                this.taskBarData = {...this.taskBarData};
                this.taskPieData.datasets[0].data = [this.taskDashboard?.totalHighPriorityTasks,
                                                     this.taskDashboard?.totalMediumPriorityTasks,
                                                     this.taskDashboard?.totalLowPriorityTasks,];
                this.taskPieData = {...this.taskPieData};
                this.taskLineData.labels = this.taskDashboard?.taskByDate.map((item) =>
                    `${this.monthNames[item.month - 1]}, ${item.year}`);
                this.taskLineData.datasets[0].data = this.taskDashboard?.taskByDate.map((item) =>
                    item.completedTasks);
                this.taskLineData.datasets[1].data = this.taskDashboard?.taskByDate.map((item) =>
                    item.completedWithDelayTasks);
                this.taskLineData.datasets[2].data = this.taskDashboard?.taskByDate.map((item) =>
                    item.inProgressTasks);
                this.taskLineData.datasets[3].data = this.taskDashboard?.taskByDate.map((item) =>
                    item.pendingTasks);
                this.taskLineData.datasets[4].data = this.taskDashboard?.taskByDate.map((item) =>
                    item.delayedTasks);
                this.taskLineData = {...this.taskLineData};
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public getProjectDashboard() {
        this.dashboardService.getProjectDashboard(this.currentDateFrom.toISOString(),
            this.currentDateTo.toISOString(),).subscribe({
            next: (response) => {
                this.projectDashboard = response.data;
                this.projectBarData.datasets[0].data = [this.projectDashboard?.totalCompletedProjects,
                                                        this.projectDashboard?.totalCompletedWithDelayProjects,
                                                        this.projectDashboard?.totalInProgressProjects,
                                                        this.projectDashboard?.totalDelayedProjects,];
                this.projectBarData = {...this.projectBarData};

                this.projectLineData.labels = this.projectDashboard?.projectByDate.map((item) =>
                    `${this.monthNames[item.month - 1]}, ${item.year}`);
                this.projectLineData.datasets[0].data = this.projectDashboard?.projectByDate.map((item) =>
                    item.completedProjects);
                this.projectLineData.datasets[1].data = this.projectDashboard?.projectByDate.map((item) =>
                    item.completedWithDelayProjects);
                this.projectLineData.datasets[2].data = this.projectDashboard?.projectByDate.map((item) =>
                    item.inProgressProjects);
                this.projectLineData.datasets[3].data = this.projectDashboard?.projectByDate.map((item) =>
                    item.delayedProjects);
                this.projectLineData = {...this.projectLineData};
            }, error: (error) => {
                console.error(error);
                this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
            }
        });
    }

    public onDateFromChange(event: any) {
        if (event > this.currentDateTo) {
            this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'La fecha de inicio no puede ser mayor a la fecha de finalización'
            });
            return;
        }
        this.currentDateFrom = event;
        this.getTaskDashboard();
        this.getProjectDashboard();
    }

    public onDateToChange(event: any) {
        if (event < this.currentDateFrom) {
            this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'La fecha de finalización no puede ser menor a la fecha de inicio'
            });
            return;
        }
        this.currentDateTo = event;
        this.getTaskDashboard();
        this.getProjectDashboard();
    }
}
