package com.example.endavapwj.DTOs.TestCaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTestCaseDTO {
  private Long problemId;
  private String problemTitle;
  private String input;
  private String output;
}
