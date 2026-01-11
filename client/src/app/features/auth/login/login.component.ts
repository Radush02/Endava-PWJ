import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { LoginDTO } from '../../../core/models/login.request';
import { AuthService } from '../../../core/auth/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  constructor(private authService: AuthService) { }

  loginData: LoginDTO = {
    username: '',
    password: ''
  };
  errorMessage: string = '';
  onSubmit(loginForm: any) {
    this.authService.login(this.loginData).subscribe(
      response => {
        console.log('Login successful', response);
        this.errorMessage = "logged in";
      },
      error => {
        console.error('Login failed', error);
        this.errorMessage = "login failed";
      }
    );
  }
}
