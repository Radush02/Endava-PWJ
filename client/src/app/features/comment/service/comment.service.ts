import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { AddCommentDTO, AddCommentVoteDTO, CommentVoteType } from '../models/comment.request';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { 
  }

  addComment(comment:AddCommentDTO){
    return this.http.post<Record<string,string>>(`${this.apiUrl}/comment`, comment, { withCredentials: true });
  }
  addVote(vote:AddCommentVoteDTO){
    return this.http.post<Record<string,string>>(`${this.apiUrl}/comment/vote`, vote, { withCredentials: true });
  }
}
