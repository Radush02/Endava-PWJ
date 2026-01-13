package com.example.endavapwj.DTOs.ProblemDTO;

import com.example.endavapwj.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class FilterDTO {
    Difficulty difficulty;
    String title;
}
