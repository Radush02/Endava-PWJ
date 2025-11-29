package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.CreateTestCaseDTO;
import com.example.endavapwj.services.TestCaseService.TestCaseService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/testcase")
public class TestCaseController {

  private final TestCaseService testCaseService;

  public TestCaseController(TestCaseService testCaseService) {
    this.testCaseService = testCaseService;
  }

  @PostMapping("/create")
  public CompletableFuture<ResponseEntity<Map<String, String>>> createTestCase(
      @RequestBody CreateTestCaseDTO createTestCaseDTO) {
    return testCaseService
        .addTestCase(createTestCaseDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }
}
