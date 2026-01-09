package com.example.endavapwj.DTOs.SubmissionDTO;

import com.example.endavapwj.DTOs.ProblemDTO.ProblemDTO;
import com.example.endavapwj.enums.Language;
import com.example.endavapwj.enums.Verdict;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SubmissionDTO {
  String submissionId;
  ProblemDTO problem;
  String username;
  Verdict verdict;
  Language language;
  String output;
  String expectedOutput;
  Instant createdAt;
  Instant finishedAt;
}
