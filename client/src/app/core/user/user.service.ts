import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { UserDTO } from '../models/user.request';
import {firstValueFrom} from "rxjs/internal/firstValueFrom";
@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  me() {
    return this.http.get<UserDTO>(`${this.apiUrl}/user/me`, { withCredentials: true });
  }
}
