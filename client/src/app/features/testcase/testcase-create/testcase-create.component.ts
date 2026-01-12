import { Component, OnInit } from '@angular/core';
import { TestCaseService } from '../service/testcase.service';
import { CreateTestcaseDTO } from '../models/testcase.request';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-testcase-create',
  imports: [CommonModule,FormsModule],
  templateUrl: './testcase-create.component.html',
  styleUrl: './testcase-create.component.css'
})
export class TestcaseCreateComponent implements OnInit {

  constructor(private testCaseService: TestCaseService) { }
  ngOnInit(): void {
  }
  errorMessage:string="";
  testcase:CreateTestcaseDTO={
    input:"",
    output:"",
    problemId:-1,
    problemTitle:""
  }
  problemId:number|null=null;
  createTestcase(){
    this.testcase.problemId=this.problemId!=null?this.problemId:this.testcase.problemId;
    this.testCaseService.createTestcase(this.testcase).subscribe({
      next:(response)=>{
        console.log("Testcase created successfully", response);
        this.errorMessage="Testcase created successfully";
      },
      error:(error)=>{
        console.error("Error creating testcase", error);
        this.errorMessage="Error creating testcase";
      } 
    });
  }
}
