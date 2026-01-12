package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.SubmissionDTO.SubmissionDTO;
import com.example.endavapwj.DTOs.SubmissionDTO.SubmitCodeDTO;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.services.SubmissionService.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submission")
@RestController
@RequestMapping("/api/v2/submission")
public class SubmissionController {
  private final SubmissionService submissionService;

  public SubmissionController(SubmissionService submissionService) {
    this.submissionService = submissionService;
  }

  @Operation(
      summary = "Submit a solution",
      description =
          "Takes the details of a submission like problem id, source code and language and creates it. "
              + "Sends the submission to the judge for evaluation. "
              + "Returns a message and the submission id if valid. If the problem doesn't exist or user is not found throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "202",
            description = "Submission accepted and pending judging",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Problem or user not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @PostMapping("/submit")
  public CompletableFuture<ResponseEntity<Map<String, String>>> submit(
      @RequestBody SubmitCodeDTO submitCodeDTO) {
    return submissionService
        .createSubmission(submitCodeDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.ACCEPTED).body(body));
  }

  @Operation(
      summary = "Gets a specific submission",
      description =
          "Returns the submission if valid. If the submission doesn't exist or the user is not found throws exception."
              + "Should be used with polling after submitting the solution.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Submission fetched successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubmissionDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Submission or user not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @GetMapping("/{id}")
  public CompletableFuture<ResponseEntity<SubmissionDTO>> getSubmissionById(
      @PathVariable String id) {
    return submissionService
        .getSubmission(id)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
