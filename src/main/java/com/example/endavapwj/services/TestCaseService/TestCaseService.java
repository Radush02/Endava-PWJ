package com.example.endavapwj.services.TestCaseService;

import com.example.endavapwj.DTOs.CreateTestCaseDTO;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TestCaseService {
  @Transactional
  CompletableFuture<Map<String, String>> addTestCase(CreateTestCaseDTO createTestCaseDTO);
}
