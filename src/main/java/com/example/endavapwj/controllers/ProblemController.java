package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.ProblemDTO.CreateProblemDTO;
import com.example.endavapwj.DTOs.ProblemDTO.FullProblemDTO;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.services.ProblemService.ProblemService;
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

@Tag(name = "Problem")
@RestController
@RequestMapping("/api/v2/problem")
public class ProblemController {

  private final ProblemService problemService;

  public ProblemController(ProblemService problemService) {
    this.problemService = problemService;
  }

  @Operation(
      summary = "Create a problem",
      description =
          "Takes the details of a problem and creates it."
              + "Returns a creation message if valid. If the user's not logged in or not an admin throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Email validated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "403",
            description = "User's not an admin",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotPermittedException.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User's not logged in",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @PostMapping("/create")
  public CompletableFuture<ResponseEntity<Map<String, String>>> create(
      @RequestBody CreateProblemDTO createProblemDTO) {
    return problemService
        .create(createProblemDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @Operation(
      summary = "Gets a specific problem",
      description = "Returns the problem if valid. If the problem doesn't exist throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Problem fetched successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Problem doesn't exist",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotFoundException.class)))
      })
  @GetMapping("/{id}")
  public CompletableFuture<ResponseEntity<FullProblemDTO>> get(@PathVariable Long id) {

    return problemService
        .getById(id)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }

  @Operation(
      summary = "Gets a list of problems",
      description =
          "Gets a list of problem, based on the page and the amount of problems requested."
              + "Returns a list of problems. May be empty.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Problems fetched successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
      })
  @GetMapping()
  public CompletableFuture<ResponseEntity<List<FullProblemDTO>>> getAll(
      @RequestParam Integer page, @RequestParam Integer size) {
    return problemService
        .getAllProblems(page, size)
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
