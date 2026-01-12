package com.example.endavapwj.DTOs.CommentDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentDTO {
  Long id;
  String username;
  String comment;
  Integer upvotes;
  Integer downvotes;
}
