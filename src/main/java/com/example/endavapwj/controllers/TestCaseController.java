package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.TestCaseDTO.CreateTestCaseDTO;
import com.example.endavapwj.DTOs.TestCaseDTO.TestCaseDTO;
import com.example.endavapwj.DTOs.TestCaseDTO.TestCaseIdDTO;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.services.TestCaseService.TestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Test Case")
@RestController
@RequestMapping("/api/v2/testcase")
public class TestCaseController {

  private final TestCaseService testCaseService;

  public TestCaseController(TestCaseService testCaseService) {
    this.testCaseService = testCaseService;
  }

  @Operation(
      summary = "Create a test case",
      description =
          "Takes the details of a test case and creates it for the given problem. "
              + "Only the author of the problem can add test cases. "
              + "Returns a message when done. If the problem or user doesn't exist or the user is not the author throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Test case created successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "403",
            description = "User is not the author of the problem",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotPermittedException.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User or problem not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @PostMapping("/create")
  public CompletableFuture<ResponseEntity<Map<String, String>>> createTestCase(
      @RequestBody CreateTestCaseDTO createTestCaseDTO) {
    return testCaseService
        .addTestCase(createTestCaseDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @Operation(
      summary = "Gets test cases of a problem",
      description =
          "Returns the list of test cases for a given problem id. "
              + "If the problem doesn't exist throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Test cases fetched successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TestCaseDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Problem not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @GetMapping("/byProblem/{id}")
  public CompletableFuture<ResponseEntity<List<TestCaseDTO>>> getTestCaseByProblem(
      @PathVariable Long id) {
    return testCaseService
        .getTestCases(id)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }

  @Operation(
      summary = "Delete a test case",
      description =
          "Deletes a test case by id. Only accessible for the problem author. "
              + "Returns a delete message. If the test case doesn't exist throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Test case deleted successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Test case not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @DeleteMapping("/{id}")
  public CompletableFuture<ResponseEntity<Map<String, String>>> deleteTestCase(
      @PathVariable Long id) {
    return testCaseService
        .deleteTestCase(id)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }

  @Operation(
      summary = "Edit a test case",
      description =
          "Updates the input and output of an existing test case. "
              + "Only the author of the problem can edit test cases. "
              + "Returns a message when done. If the test case doesn't exist or the user is not allowed throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Test case updated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "403",
            description = "User is not the author of the problem",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotPermittedException.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Test case not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @PutMapping("/edit")
  public CompletableFuture<ResponseEntity<Map<String, String>>> editTestCase(
      @RequestBody TestCaseIdDTO testCaseUpdateDTO) {

    return testCaseService
        .editTestCase(testCaseUpdateDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
