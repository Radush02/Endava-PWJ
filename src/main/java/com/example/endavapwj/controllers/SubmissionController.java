package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.SubmitCodeDTO;
import com.example.endavapwj.services.SubmissionService.SubmissionService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v2/submissions")
public class SubmissionController {
  private final SubmissionService submissionService;

  public SubmissionController(SubmissionService submissionService) {
    this.submissionService = submissionService;
  }

  @PostMapping("/submit")
  public CompletableFuture<ResponseEntity<Map<String, String>>> submit(
      @RequestBody SubmitCodeDTO submitCodeDTO) {
    return submissionService
        .createSubmission(submitCodeDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.ACCEPTED).body(body));
  }
}
