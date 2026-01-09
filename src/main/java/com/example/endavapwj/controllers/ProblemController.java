package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.ProblemDTO.EditProblemDTO;
import com.example.endavapwj.DTOs.ProblemDTO.FullProblemDTO;
import com.example.endavapwj.services.ProblemService.ProblemService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/problem")
public class ProblemController {

  private final ProblemService problemService;

  public ProblemController(ProblemService problemService) {
    this.problemService = problemService;
  }

  @PostMapping("/create")
  public CompletableFuture<ResponseEntity<Map<String, String>>> create(
      @RequestBody EditProblemDTO.CreateProblemDTO createProblemDTO) {
    return problemService
        .create(createProblemDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @GetMapping("/{id}")
  public CompletableFuture<ResponseEntity<FullProblemDTO>> get(@PathVariable Long id) {

    return problemService.getById(id)
            .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
