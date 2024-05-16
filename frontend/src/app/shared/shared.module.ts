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
import {TimeAgoPipe} from "../core/pipes/time-ago.pipe";
import {FileSizePipe} from "../core/pipes/file-size.pipe";

@NgModule({
  declarations: [
    ForbiddenComponent,
    NotFoundComponent,
    TimeAgoPipe,
    FileSizePipe
],
  exports: [
    TimeAgoPipe,
    FileSizePipe
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
