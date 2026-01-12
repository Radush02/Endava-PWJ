import { Component, Input, OnInit } from '@angular/core';
import { CommentService } from '../service/comment.service';
import {AddCommentVoteDTO, CommentDTO, CommentVoteType } from '../models/comment.request';

@Component({
  selector: 'app-comment',
  imports: [],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.css'
})
export class CommentComponent {
  @Input() comment!: CommentDTO;
  CommentVoteType = CommentVoteType;
  constructor(private commentService: CommentService) { }

  addVote(voteType:CommentVoteType){
    let vote:AddCommentVoteDTO={
    commentId:this.comment.id,
    voteType:voteType
    }
    this.commentService.addVote(vote).subscribe({
      next:(response)=>{
        console.log("Vote added successfully", response);
      },
      error:(error)=>{
        console.error("Error adding vote", error);
      } 
    });
  }
}
