import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ForbiddenComponent} from './components/forbidden/forbidden.component';
import {SidebarModule} from "primeng/sidebar";
import {RadioButtonModule} from "primeng/radiobutton";
import {InputSwitchModule} from "primeng/inputswitch";
import {FormsModule} from "@angular/forms";
import {NotFoundComponent} from "./components/not-found/not-found.component";
import {ButtonModule} from "primeng/button";
import {RouterLink} from "@angular/router";
import {TimeAgoPipe} from "./pipes/time-ago.pipe";
import {FileSizePipe} from "./pipes/file-size.pipe";
import {BreakLinePipe} from "./pipes/breakline.pipe";

@NgModule({
    declarations: [
        ForbiddenComponent,
        NotFoundComponent,
        TimeAgoPipe,
        FileSizePipe,
        BreakLinePipe
    ],
    exports: [
        TimeAgoPipe,
        FileSizePipe,
        BreakLinePipe
    ],
    imports: [
        CommonModule,
        SidebarModule,
        RadioButtonModule,
        InputSwitchModule,
        FormsModule,
        ButtonModule,
        RouterLink
    ]
})
export class SharedModule {
}
