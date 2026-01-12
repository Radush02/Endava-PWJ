import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { CreateTestcaseDTO } from '../models/testcase.request';

@Injectable({
  providedIn: 'root'
})
export class TestCaseService {

  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { 
  }
  createTestcase(testcase:CreateTestcaseDTO){
    return this.http.post<Record<string,string>>(`${this.apiUrl}/testcase/create`, testcase, { withCredentials: true });
  }
}
