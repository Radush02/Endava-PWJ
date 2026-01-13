package com.example.endavapwj.services.SubmissionService;

import com.example.endavapwj.DTOs.SubmissionDTO.SubmissionDTO;
import com.example.endavapwj.DTOs.SubmissionDTO.SubmitCodeDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SubmissionService {

  CompletableFuture<Map<String, String>> createSubmission(SubmitCodeDTO submitCodeDTO);

  CompletableFuture<SubmissionDTO> getSubmission(String id);

    CompletableFuture<List<SubmissionDTO>> getBestSubmissions(Long problemId);
}
