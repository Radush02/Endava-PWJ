import { Component } from '@angular/core';
import { ListProblemComponent } from '../list-problem/list-problem.component';

@Component({
  selector: 'app-problem-home',
  imports: [ListProblemComponent],
  templateUrl: './problem-home.component.html',
  styleUrl: './problem-home.component.css'
})
export class ProblemHomeComponent {

}
