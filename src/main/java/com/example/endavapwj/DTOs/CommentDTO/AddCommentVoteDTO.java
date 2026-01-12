package com.example.endavapwj.DTOs.CommentDTO;

import com.example.endavapwj.enums.CommentVoteType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCommentVoteDTO {
  public Long commentId;
  public CommentVoteType type;
}
