package com.example.endavapwj.DTOs.SubmissionDTO;

import com.example.endavapwj.enums.Language;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitCodeDTO {
  @NotNull private Long problemId;
  private String source;
  private Language language;
}
