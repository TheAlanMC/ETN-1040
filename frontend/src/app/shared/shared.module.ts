import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import {SidebarModule} from "primeng/sidebar";
import {RadioButtonModule} from "primeng/radiobutton";
import {InputSwitchModule} from "primeng/inputswitch";
import {FormsModule} from "@angular/forms";



@NgModule({
    declarations: [
        ForbiddenComponent
    ],
    exports: [
    ],
    imports: [
        CommonModule,
        SidebarModule,
        RadioButtonModule,
        InputSwitchModule,
        FormsModule
    ]
})
export class SharedModule { }
