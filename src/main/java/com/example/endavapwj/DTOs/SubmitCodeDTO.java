package com.example.endavapwj.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitCodeDTO {
  @NotNull private Long problemId;
  private String source;
}
