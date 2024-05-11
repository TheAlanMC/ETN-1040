import {Component, OnInit} from '@angular/core';
import {UserDto} from "../../models/user.dto";
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../../../core/services/user.service";
import {Location} from "@angular/common";

@Component({
  selector: 'app-view-user',
  templateUrl: './view-user.component.html',
  styleUrl: './view-user.component.scss',
})
export class ViewUserComponent implements OnInit {

  userId: number = 0;

  profilePictureUrl: string = 'assets/layout/images/avatar.png';

  user: UserDto | null = null;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private location: Location) {
  }

  ngOnInit() {
    this.userId = this.activatedRoute.snapshot.params['id'];
    this.getUserInfo();
    this.getUserProfilePictureUrl();
  }

  public getUserProfilePictureUrl(){
    this.userService.getUserProfilePicture(this.userId).subscribe({
      next: (data) => {
        this.profilePictureUrl = URL.createObjectURL(data);
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public getUserInfo(){
    this.userService.getUser(this.userId).subscribe({
      next: (data) => {
        this.user = data.data;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  public onBack() {
    this.location.back();
  }
}
