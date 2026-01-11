import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { LoginDTO, LoginResultDTO } from '../models/login.request';
import { RegisterDTO } from '../models/register.request';
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  login(credentials: LoginDTO) {
    return this.http.post<LoginResultDTO>(`${this.apiUrl}/auth/login`, credentials,{withCredentials: true});
  }

  register(credentials: RegisterDTO){
    return this.http.post<Record<string,string>>(`${this.apiUrl}/auth/register`, credentials,{withCredentials: true});
  }
  
  validate(token: string) {
    return this.http.post<Record<string, string>>(`${this.apiUrl}/auth/validate/${token}`, {withCredentials: true});
  }

  logout() {
    return this.http.post<Record<string, string>>(`${this.apiUrl}/auth/logout`, {withCredentials: true});
  }

  loggedIn(){
    return this.http.get<Record<string, string>>(`${this.apiUrl}/auth`,{withCredentials: true});
  }
}
