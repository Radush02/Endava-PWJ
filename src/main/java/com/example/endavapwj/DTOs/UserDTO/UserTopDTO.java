package com.example.endavapwj.DTOs.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTopDTO {
        String username;
        Long totalSolved;
        Long easySolved;
        Long mediumSolved;
        Long hardSolved;
}
