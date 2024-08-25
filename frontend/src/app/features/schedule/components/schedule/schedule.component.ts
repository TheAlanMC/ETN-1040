import {Component, OnInit, ViewChild} from '@angular/core';
import {FullCalendarComponent} from "@fullcalendar/angular";
import {MessageService, SelectItem} from "primeng/api";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import {SemesterService} from "../../../../core/services/semester.service";
import {SemesterDto} from "../../models/semester.dto";
import {AssistantScheduleService} from "../../../../core/services/assistant-schedule.service";
import {ScheduleService} from "../../../../core/services/schedule.service";
import {ScheduleDto} from "../../models/schedule.dto";
import {AssistantDto} from "../../models/assistant.dto";
import esLocale from "@fullcalendar/core/locales/es";

@Component({
    selector: 'app-schedule',
    templateUrl: './schedule.component.html',
    styleUrl: './schedule.component.scss',
    providers: [
        MessageService
    ],
})
export class ScheduleComponent implements OnInit {

    @ViewChild('calendar') calendarComponent!: FullCalendarComponent;

    calendarOptions: any;

    canManageSchedule = false;

    semesterItems: SelectItem[] = [];
    selectedSemester: number = 0;

    semesters: SemesterDto[] = [];

    schedules: ScheduleDto[] = [];

    assistantsSchedule: AssistantDto[] = [];
    assistants: AssistantDto[] = [];

    isLoading: boolean = false;

    colors = ['#87CEEB', '#87CEFA', '#00BFFF', '#1E90FF', '#6495ED', '#4682B4', "#3b82f6"];

    enableSaveButton = false;

    selectedSchedule: number = 0;
    schedule: ScheduleDto | undefined;
    showAssistantDialog = false;

    selectedAssistant: number = 0;
    assistantItems: SelectItem[] = [];

    constructor(
        private semesterService: SemesterService,
        private assistantScheduleService: AssistantScheduleService,
        private scheduleService: ScheduleService,
        private messageService: MessageService
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        if (token) {
            const decoded = jwtDecode<JwtPayload>(token!);
            if (decoded.permissions.includes('CREAR HORARIOS') || decoded.permissions.includes('EDITAR HORARIOS')) {
                this.canManageSchedule = true;
            }
        }
    }

    ngOnInit() {
        // Initialize the today variable with the current date
        this.calendarOptions = {
            plugins: [
                dayGridPlugin,
                timeGridPlugin,
                interactionPlugin
            ],
            height: 480,
            initialDate: new Date(),
            initialView: 'timeGridWeek',
            views: {
                timeGridWeek: {
                    titleFormat: {year: 'numeric', month: 'long', day: 'numeric'},
                    dayHeaderFormat: {weekday: 'long'},
                }
            },
            headerToolbar: {
                left: '', // Remove prev, next, and today buttons
                center: 'title', // Keep the title in the center
                right: 'timeGridWeek' // Only show the weekly view button
            },
            locale: esLocale,
            editable: false,
            selectable: false,
            selectMirror: true,
            dayMaxEvents: true,
            eventDurationEditable: false,
            hiddenDays: [0, 6], // Hide Sunday (0) and Saturday (6)
            slotMinTime: '08:00:00',
            slotMaxTime: '18:00:00',
            allDaySlot: false,
            slotLabelFormat: {
                hour: 'numeric',
                minute: '2-digit',
                omitZeroMinute: false,
            },
            eventClick: (e: any) => this.onEventClick(e),
        };
        this.getSemesters();
        this.getSchedules();
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
                this.selectedSemester = this.semesters[0].semesterId;
                this.getAssistantsScheduleBySemester(this.semesters[0].semesterId);
                this.getSemesterAssistants(this.semesters[0].semesterId);
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public getSchedules() {
        this.scheduleService.getSchedules().subscribe({
            next: (data) => {
                this.schedules = data.data!;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public getAssistantsScheduleBySemester(semesterId: number) {
        this.isLoading = true;
        this.assistantScheduleService.getSchedulesBySemesterId(semesterId).subscribe({
            next: (data) => {
                this.assistantsSchedule = data.data!;
                const events = this.generateEventsForCurrentWeek();
                let calendarApi = this.calendarComponent.getApi();
                calendarApi.removeAllEvents();
                calendarApi.addEventSource(events);
                this.isLoading = false;
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public onSelectSemester(event: any) {
        this.getAssistantsScheduleBySemester(event.value);
        this.getSemesterAssistants(event.value);
        this.enableSaveButton = false;
        this.selectedSchedule = 0;
        this.schedule = undefined;
    }

    public getSemesterAssistants(semesterId: number) {
        this.semesterService.getSemesterAssistants(semesterId).subscribe({
            next: (data) => {
                this.assistants = data.data!;
                this.assistantItems = [];
                this.assistantItems.push({
                    label: 'No Asignado',
                    value: 0
                });
                this.assistants.forEach(assistant => {
                    this.assistantItems.push({
                        label: `${assistant.assistant.firstName} ${assistant.assistant.lastName}`,
                        value: assistant.assistantId
                    });
                });

            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public generateEventsForCurrentWeek() {
        const events: any = [];
        const currentDate = new Date();
        if (currentDate.getDay() === 0) {
            currentDate.setDate(currentDate.getDate() - 7);
        }
        const currentWeekStart = new Date(currentDate.setDate(currentDate.getDate() - currentDate.getDay()));
        const assistantColors: { [key: number]: string } = {};
        let colorIndex = 0;

        this.schedules.forEach(schedule => {
            const dayOfWeek = schedule.dayNumber;

            const [startHour, startMinute] = schedule.hourFrom.split(':').map(Number);
            const [endHour, endMinute] = schedule.hourTo.split(':').map(Number);

            const startDate = new Date(currentWeekStart);
            startDate.setDate(currentWeekStart.getDate() + dayOfWeek);
            startDate.setHours(startHour,
                startMinute,
                0);

            const endDate = new Date(currentWeekStart);
            endDate.setDate(currentWeekStart.getDate() + dayOfWeek);
            endDate.setHours(endHour,
                endMinute,
                0);


            const assistant = this.assistantsSchedule.find(assistant => assistant.schedules.some(s => s.scheduleId === schedule.scheduleId));
            if (assistant) {
                if (!assistantColors[assistant.assistantId]) {
                    assistantColors[assistant.assistantId] = this.colors[colorIndex % this.colors.length];
                    colorIndex++;
                }
            }
            events.push({
                id: schedule.scheduleId,
                title: (assistant !== undefined) ? `${assistant?.assistant.firstName} ${assistant?.assistant.lastName}` : 'No Asignado',
                start: startDate,
                end: endDate,
                backgroundColor: (assistant !== undefined) ? assistantColors[assistant.assistantId] : assistantColors[6],
                borderColor: (assistant !== undefined) ? assistantColors[assistant.assistantId] : assistantColors[6],
            });
        });
        return events;
    }

    public generateEventsForCustomWeek() {
        const events: any = [];
        const currentDate = new Date();
        if (currentDate.getDay() === 0) {
            currentDate.setDate(currentDate.getDate() - 7);
        }
        const currentWeekStart = new Date(currentDate.setDate(currentDate.getDate() - currentDate.getDay()));
        const assistantColors: { [key: number]: string } = {};
        let colorIndex = 0;

        this.schedules.forEach(schedule => {
            const dayOfWeek = schedule.dayNumber;

            const [startHour, startMinute] = schedule.hourFrom.split(':').map(Number);
            const [endHour, endMinute] = schedule.hourTo.split(':').map(Number);

            const startDate = new Date(currentWeekStart);
            startDate.setDate(currentWeekStart.getDate() + dayOfWeek);
            startDate.setHours(startHour,
                startMinute,
                0);

            const endDate = new Date(currentWeekStart);
            endDate.setDate(currentWeekStart.getDate() + dayOfWeek);
            endDate.setHours(endHour,
                endMinute,
                0);

            const assistant = this.assistants.sort((
                a,
                b
            ) => a.assistantId - b.assistantId).find(assistant => assistant.scheduleIds.includes(schedule.scheduleId));
            if (assistant) {
                if (!assistantColors[assistant.assistantId]) {
                    assistantColors[assistant.assistantId] = this.colors[colorIndex % this.colors.length];
                    colorIndex++;
                }
            }
            events.push({
                id: schedule.scheduleId,
                title: (assistant !== undefined) ? `${assistant?.assistant.firstName} ${assistant?.assistant.lastName}` : 'No Asignado',
                start: startDate,
                end: endDate,
                backgroundColor: (assistant !== undefined) ? assistantColors[assistant.assistantId] : assistantColors[6],
                borderColor: (assistant !== undefined) ? assistantColors[assistant.assistantId] : assistantColors[6],
            });
        });
        return events;
    }

    public createRandomSchedule() {
        this.assistantScheduleService.createRandomSchedule(this.selectedSemester).subscribe({
            next: (data) => {
                this.getAssistantsScheduleBySemester(this.selectedSemester);
                this.messageService.add({
                    severity: 'success',
                    summary: 'Horarios creados',
                    detail: 'Se han creado los horarios de forma aleatoria'
                });
            }, error: (error) => {
                console.error(error);
            }
        });
    }

    public editSchedule() {
        this.enableSaveButton = true;
        this.assistants.forEach(assistant => {
            assistant.scheduleIds = [];
        });
        this.assistantsSchedule.forEach(assistant => {
            assistant.schedules.forEach(schedule => {
                this.assistants.find(a => a.assistantId === assistant.assistantId)?.scheduleIds.push(schedule.scheduleId);
            });
        });
    }

    public cancelSchedule() {
        this.enableSaveButton = false;
        this.getAssistantsScheduleBySemester(this.selectedSemester);
    }

    public onEventClick(e: any) {
        if (!this.enableSaveButton) {
            return;
        }
        this.selectedSchedule = Number(e.event.id);
        const assistant = this.assistants.find(assistant => assistant.scheduleIds.includes(this.selectedSchedule));
        this.showAssistantDialog = true;
        this.schedule = this.schedules.find(schedule => schedule.scheduleId === this.selectedSchedule);
        this.selectedAssistant = (assistant) ? assistant.assistantId : 0;
    }

    public saveAssistant() {
        const assistant = this.assistants.find(assistant => assistant.assistantId === this.selectedAssistant);
        // Remove the schedule from the previous assistant
        this.assistants.forEach(a => {
            a.scheduleIds = a.scheduleIds.filter(id => id !== this.selectedSchedule);
        });
        // Add the schedule to the new assistant
        if (assistant) {
            assistant.scheduleIds.push(this.selectedSchedule);
        }
        // Update the calendar
        const events = this.generateEventsForCustomWeek();
        let calendarApi = this.calendarComponent.getApi();
        calendarApi.removeAllEvents();
        calendarApi.addEventSource(events);
        this.cancelAssistant();
    }

    public cancelAssistant() {
        this.showAssistantDialog = false
        this.selectedAssistant = 0;
        this.selectedSchedule = 0;
    }

    public saveSchedule() {
        this.assistantScheduleService.createCustomSchedule(this.selectedSemester,
            this.assistants).subscribe({
            next: (data) => {
                this.enableSaveButton = false;
                this.messageService.add({
                    severity: 'success',
                    summary: 'Horarios actualizados',
                    detail: 'Se han guardado los horarios de forma exitosa'
                });
            }, error: (error) => {
                console.error(error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: error.error.message
                });
            }
        });
    }
}
