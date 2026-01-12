package com.example.endavapwj.services.TestCaseService;

import com.example.endavapwj.DTOs.TestCaseDTO.CreateTestCaseDTO;
import com.example.endavapwj.DTOs.TestCaseDTO.TestCaseDTO;
import com.example.endavapwj.DTOs.TestCaseDTO.TestCaseIdDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TestCaseService {
  @Transactional
  CompletableFuture<Map<String, String>> addTestCase(CreateTestCaseDTO createTestCaseDTO);

  CompletableFuture<List<TestCaseDTO>> getTestCases(Long problemId);

  CompletableFuture<Map<String, String>> deleteTestCase(Long id);

  CompletableFuture<Map<String, String>> editTestCase(TestCaseIdDTO testCase);
}
