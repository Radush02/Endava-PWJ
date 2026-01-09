package com.example.endavapwj.DTOs.CommentDTO;

import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCommentDTO {
    Long problemId;
    String comment;
}
