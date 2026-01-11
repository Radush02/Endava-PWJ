import { Component, OnInit } from '@angular/core';
import { ProblemService } from '../service/problem.service';
import { FullProblemDTO, Language } from '../models/problem.request';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CreatedSubmissionDTO, SubmissionDTO, SubmitCodeDTO, Verdict } from '../models/submission.request';
import { SubmissionService } from '../service/submission.service';

@Component({
  selector: 'app-problem-view',
  imports: [FormsModule, CommonModule],
  templateUrl: './problem-view.component.html',
  styleUrl: './problem-view.component.css'
})
export class ProblemViewComponent implements OnInit {
  problemId: number = 0;
  userSubmission: string = '';
  availableLanguages: Language[] = [Language.JAVA, Language.PY, Language.CPP];
  selectedLanguage: Language | null = null;
  constructor(private problemService: ProblemService,private params:ActivatedRoute,private submissionService:SubmissionService) { 
    this.problemId = Number(this.params.snapshot.paramMap.get('id'));
  }
  problemDetails: FullProblemDTO | null = null;
  ngOnInit(): void {
    this.problemService.getProblemById(this.problemId).subscribe(
      (data: FullProblemDTO) => {
        this.problemDetails = data;
      },
      (error) => {
        console.error('Error fetching problem details', error);
      }
    );
  }

  submitSolution() {
    if (!this.selectedLanguage) {
      alert('Please select a programming language.');
      return;
    }
    const submissionPayload: SubmitCodeDTO = {
      problemId: this.problemId,
      source: this.userSubmission,
      language: this.selectedLanguage
    }
    this.submissionService.submitSolution(submissionPayload).subscribe(
      (data: CreatedSubmissionDTO) => {
        const submissionId = data.submissionId;
        this.waitForResult(submissionId).then(
          (result: SubmissionDTO) => {
            alert(`Submission Result: ${result.verdict}`);
          }
        ).catch(
          (error) => {
            console.error('Error while waiting for submission result', error);
          } 
        );
      },
      (error) => {
        console.error('Error submitting solution', error);
      }
    );

  }
    waitForResult(submissionId: string, intervalMs: number = 200): Promise<SubmissionDTO> {
    return new Promise((resolve, reject) => {
      const interval = setInterval(() => {
        this.submissionService.getSubmissionResult(submissionId).subscribe(
          (data: SubmissionDTO) => {
            if (data.verdict !== Verdict.PENDING && data.verdict !== Verdict.RUNNING) {
              clearInterval(interval);
              resolve(data);
            }
          },
          (error) => {
            clearInterval(interval);
            reject(error);
          }
        );
      }, intervalMs);
    });
  }
}
