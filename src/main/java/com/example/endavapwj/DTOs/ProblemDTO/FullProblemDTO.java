package com.example.endavapwj.DTOs.ProblemDTO;

import com.example.endavapwj.DTOs.CommentDTO.CommentDTO;
import com.example.endavapwj.enums.Difficulty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FullProblemDTO {
  Long id;
  String title;
  String description;
  Difficulty difficulty;
  Integer timeLimit;
  Integer memoryLimit;
  String author;
  List<CommentDTO> comments;
}
