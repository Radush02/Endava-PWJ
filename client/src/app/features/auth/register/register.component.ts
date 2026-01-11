import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { LoginDTO } from '../../../core/models/login.request';
import { AuthService } from '../../../core/auth/auth.service';
import { FormsModule } from '@angular/forms';
import { RegisterDTO } from '../../../core/models/register.request';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  constructor(private authService: AuthService) { }

  registerData: RegisterDTO={
    username: '',
    email: '',
    password: ''
  };
  errorMessage: string = '';
  onSubmit(registerForm: any) {
    this.authService.register(this.registerData).subscribe(
      response => {
        console.log('Register successful', response);
        this.errorMessage = "registered successfully";
      },
      error => {
        console.error('Register failed', error);
        this.errorMessage = "registration failed";
      }
    );
  }
}
