package com.example.endavapwj.DTOs.SubmissionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LeaderboardDTO {
    private String username;
    private Long total_accepted;
}
