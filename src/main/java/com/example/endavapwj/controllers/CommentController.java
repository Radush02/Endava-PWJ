package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.CommentDTO.AddCommentDTO;
import com.example.endavapwj.DTOs.CommentDTO.AddCommentVoteDTO;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.services.CommentService.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Comment",
    description = "All endpoints related to comment management (interaction, creation, etc.)")
@RestController
@RequestMapping("/api/v2/comment")
@AllArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/")
  @Operation(
      summary = "Add a comment",
      description =
          "Leave a comment on a specified problem. "
              + "Returns the comment if successful. If user's not logged in or the problem doesn't exist throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Comment added successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not logged in or problem not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  public CompletableFuture<ResponseEntity<Map<String, String>>> addComment(
      @RequestBody AddCommentDTO comment) {
    return this.commentService
        .addComment(comment)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @PostMapping("/vote")
  @Operation(
      summary = "Vote on a comment",
      description =
          "Upvote or downvote a comment. "
              + "Returns the voted option if successful."
              + " If user's not logged in, the comment doesn't exist or double votes throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Voted successfully.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not logged in or comment not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvalidFieldException.class))),
        @ApiResponse(
            responseCode = "409",
            description = "User already voted",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AlreadyExistsException.class)))
      })
  public CompletableFuture<ResponseEntity<Map<String, String>>> addVote(
      @RequestBody AddCommentVoteDTO vote) {
    return this.commentService
        .addVote(vote)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
