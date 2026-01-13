import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { FullProblemDTO } from '../models/problem.request';
import { SubmissionDTO, CreatedSubmissionDTO, SubmitCodeDTO, Verdict } from '../models/submission.request';

@Injectable({
  providedIn: 'root'
})
export class SubmissionService {

  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { 
  }
  submitSolution(submission:SubmitCodeDTO){;
    return this.http.post<CreatedSubmissionDTO>(`${this.apiUrl}/submission/submit`, submission, { withCredentials: true });

  }
  getSubmissionResult(submissionId:string){
    return this.http.get<SubmissionDTO>(`${this.apiUrl}/submission/${submissionId}`, { withCredentials: true });
  }


}
