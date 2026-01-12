import { Component, Input } from '@angular/core';
import { CommentService } from '../service/comment.service';
import { AddCommentDTO } from '../models/comment.request';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-comment',
  imports: [FormsModule, CommonModule],
  templateUrl: './add-comment.component.html',
  styleUrl: './add-comment.component.css'
})
export class AddCommentComponent {
  constructor(private commentService: CommentService) { }
  @Input() problemId!:number;

  errorMessage: string = '';
  comment:string="";
  createTestcase(){
    this.errorMessage = '';
    const comment:AddCommentDTO={
    problemId:this.problemId,
    comment:this.comment
    }
    this.commentService.addComment(comment).subscribe({
      next:(response)=>{
        console.log("Comment added successfully", response);
        comment.comment = '';
      },
      error:(error)=>{
        console.error("Error adding comment", error);
        this.errorMessage = error.error?.message || 'Error adding comment';
      } 
    });
  }
}
