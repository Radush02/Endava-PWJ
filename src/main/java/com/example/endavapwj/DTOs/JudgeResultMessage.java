package com.example.endavapwj.DTOs;

import com.example.endavapwj.enums.Verdict;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JudgeResultMessage {
  String submissionId;
  Verdict verdict;
  Integer maxTimeMs;
  Integer maxMemoryKb;
  String compileError;
  String runtimeError;
}
