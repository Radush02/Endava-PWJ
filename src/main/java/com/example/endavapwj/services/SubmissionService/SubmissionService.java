package com.example.endavapwj.services.SubmissionService;

import com.example.endavapwj.DTOs.AuthenticationDTO.SubmitCodeDTO;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SubmissionService {
  @Transactional
  CompletableFuture<Map<String, String>> createSubmission(SubmitCodeDTO submitCodeDTO);
}
