package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.CommentDTO.AddCommentDTO;
import com.example.endavapwj.DTOs.CommentDTO.AddCommentVoteDTO;
import com.example.endavapwj.services.CommentService.CommentService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v2/comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/")
    public CompletableFuture<ResponseEntity<Map<String,String>>> addComment(@RequestBody AddCommentDTO comment) {
       return this.commentService.addComment(comment).thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
    }

    @PostMapping("/vote")
    public CompletableFuture<ResponseEntity<Map<String,String>>> addVote(@RequestBody AddCommentVoteDTO vote){
        return this.commentService.addVote(vote).thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
    }
}
