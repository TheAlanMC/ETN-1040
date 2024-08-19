import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ProfileService} from "../../../../core/services/profile.service";
import {JwtPayload} from "../../../../core/models/jwt-payload.dto";
import {jwtDecode} from "jwt-decode";

@Component({
    selector: 'app-home-page', templateUrl: './home-page.component.html', styleUrl: './home-page.component.scss',
})
export class HomePageComponent implements  OnInit{


    darkMode: boolean = false;

    profilePictureUrl: string = 'assets/layout/images/avatar.png';

    decoded: JwtPayload | undefined


    constructor(
        public router: Router,
        private profileService: ProfileService
    ) {
        // Get token from local storage
        const token = localStorage.getItem('token');
        // Check if token exists
        if (token) {
            this.decoded = jwtDecode<JwtPayload>(token!);
        }
    }

    ngOnInit(): void {
        this.getProfilePictureUrl();
    }

    public getProfilePictureUrl() {
        this.profileService.getProfilePicture().subscribe({
            next: (data) => {
                this.profilePictureUrl = URL.createObjectURL(data);
            },
            error: (error) => {
                console.error(error);
            }
        });
    }
}
