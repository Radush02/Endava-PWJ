package com.example.endavapwj.DTOs.DockerDTO;

import com.example.endavapwj.enums.Verdict;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JudgeResultMessageDTO {
  String submissionId;
  Verdict verdict;
  Integer maxTimeMs;
  Integer maxMemoryKb;
  String compileError;
  String runtimeError;
}
