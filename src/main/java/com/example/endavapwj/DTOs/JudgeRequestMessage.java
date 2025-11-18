package com.example.endavapwj.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JudgeRequestMessage {
  private String submissionId;
  private Long problemId;
  private Integer memoryLimit;
  private Integer timeLimit;
}
