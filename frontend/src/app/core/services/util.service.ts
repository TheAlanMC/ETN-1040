import {Injectable} from '@angular/core';
import {Capacitor} from '@capacitor/core';
import {HttpHeaders} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class UtilService {

    constructor() {
    }

    public checkIfMobile(): boolean {
        const platform = Capacitor.getPlatform();
        return platform === 'ios' || platform === 'android';
    }

    public getHttpOptions(responseType: 'json' | 'blob' = 'json'): Object {
        const token = localStorage.getItem('token') || '';
        return {
            headers: new HttpHeaders({
                'Authorization': `Bearer ${token}`
            }), responseType
        };
    }
}
