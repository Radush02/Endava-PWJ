package com.example.endavapwj.DTOs;

import com.example.endavapwj.enums.Difficulty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProblemDTO {
  @NotBlank
  @Size(max = 255)
  private String title;

  @NotBlank private String description;

  @NotNull private Difficulty difficulty;

  @NotNull @Positive private Integer memoryLimit;

  @NotNull @Positive private Integer timeLimit;
}
