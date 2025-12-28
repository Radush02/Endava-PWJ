package com.example.endavapwj.DTOs.ProblemDTO;

import com.example.endavapwj.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProblemDTO {
  @NotNull
  @Size(max = 255)
  private String title;

  private String description;
  private Difficulty difficulty;
  @Positive private Integer memoryLimit;
  @Positive private Integer timeLimit;

  @Getter
  @Setter
  public static class CreateProblemDTO {
    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank private String description;

    @NotNull private Difficulty difficulty;

    @NotNull @Positive private Integer memoryLimit;

    @NotNull @Positive private Integer timeLimit;
  }
}
