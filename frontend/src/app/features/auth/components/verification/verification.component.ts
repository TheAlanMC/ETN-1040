import {Component, OnInit} from '@angular/core';
import {LayoutService} from "../../../../layout/service/app.layout.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../../../core/services/auth.service";
import {SharedService} from "../../../../core/services/shared.service";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrl: './verification.component.scss',
  providers: [MessageService]
})
export class VerificationComponent implements OnInit {
  val1!: number;

  val2!: number;

  val3!: number;

  val4!: number;

  email!: string;

  constructor(private layoutService: LayoutService, private route: ActivatedRoute, private authService: AuthService, private router: Router, private sharedService: SharedService, private messageService: MessageService) {
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.email = this.sharedService.getData('email');
      if (this.email == null) {
        console.log('No email found');
        this.router.navigate(['/auth/login']).then(r => console.log('Redirect to login'));
      }
    });
  }

  onDigitInput(event: any) {
    let element;
    if (event.code !== 'Backspace')
      if (event.code.includes('Numpad') || event.code.includes('Digit')) {
        element = event.srcElement.nextElementSibling;
      }
    if (event.code === 'Backspace')
      element = event.srcElement.previousElementSibling;

    if (element == null) return;
    else element.focus();
  }

  get dark(): boolean {
    return this.layoutService.config().colorScheme !== 'light';
  }

  onVerify() {
    const code = this.val1.toString() + this.val2.toString() + this.val3.toString() + this.val4.toString();
    this.authService.verification(this.email, code).subscribe({
      next: (data) => {
        this.sharedService.changeData('email', this.email);
        this.sharedService.changeData('code', code);
        this.messageService.add({severity: 'success', summary: 'Éxito', detail: 'Código verificado'});
        setTimeout(() => {
          this.router.navigate(['/auth/new-password']).then(r => console.log('Redirect to new password'));
        }, 1000);
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error.message});
      }
    });
  }


}
