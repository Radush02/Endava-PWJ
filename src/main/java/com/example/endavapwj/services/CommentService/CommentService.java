package com.example.endavapwj.services.CommentService;

import com.example.endavapwj.DTOs.CommentDTO.AddCommentDTO;
import com.example.endavapwj.DTOs.CommentDTO.AddCommentVoteDTO;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CommentService {

    CompletableFuture<Map<String,String>> addComment(AddCommentDTO comment);

    CompletableFuture<Map<String,String>> addVote(AddCommentVoteDTO voteDTO);
}
