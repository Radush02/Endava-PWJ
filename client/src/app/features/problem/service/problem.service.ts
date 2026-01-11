import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { FullProblemDTO } from '../models/problem.request';

@Injectable({
  providedIn: 'root'
})
export class ProblemService {

  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { 

  }
    getAllProblems(page: number, size: number) {
      return this.http.get<FullProblemDTO[]>(`${this.apiUrl}/problem?page=${page}&size=${size}`, { withCredentials: true });
  }
  getProblemById(problemId: number) {
    return this.http.get<FullProblemDTO>(`${this.apiUrl}/problem/${problemId}`, { withCredentials: true });
  }
}
