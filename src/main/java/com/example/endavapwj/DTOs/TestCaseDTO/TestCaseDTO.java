package com.example.endavapwj.DTOs.TestCaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TestCaseDTO {
  private Long problemId;
  private String input;
  private String output;
}
