import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullProblemDTO } from '../models/problem.request';
import { ProblemService } from '../service/problem.service';

@Component({
  selector: 'app-list-problem',
  imports: [CommonModule],
  templateUrl: './list-problem.component.html',
  styleUrl: './list-problem.component.css'
})
export class ListProblemComponent implements OnInit {

  constructor(private problemService: ProblemService) { }
  problems: FullProblemDTO[] = [];
  ngOnInit(): void {
    this.getProblems(0,10);
  }
  getProblems(page: number, size: number) {
    this.problemService.getAllProblems(page, size).subscribe(
      (data: FullProblemDTO[]) => {
        this.problems = data;
      },
      (error) => {
        console.error('Error fetching problems', error);
      }
    );
  }

  redirectToProblemDetail(problemId: number) {
    console.log("Redirecting to problem with ID:", problemId);
    window.location.href = `/problem/${problemId}`;
  }
}
