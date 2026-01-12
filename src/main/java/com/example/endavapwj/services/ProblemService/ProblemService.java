package com.example.endavapwj.services.ProblemService;

import com.example.endavapwj.DTOs.ProblemDTO.CreateProblemDTO;
import com.example.endavapwj.DTOs.ProblemDTO.EditProblemDTO;
import com.example.endavapwj.DTOs.ProblemDTO.FullProblemDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ProblemService {

  @Transactional
  CompletableFuture<Map<String, String>> create(CreateProblemDTO createProblemDTO);

  @Transactional
  CompletableFuture<Map<String, String>> edit(EditProblemDTO editProblemDTO);

  @Transactional
  CompletableFuture<Map<String, String>> delete(String title);

  CompletableFuture<List<FullProblemDTO>> getAllProblems(int page, int size);

  CompletableFuture<FullProblemDTO> getById(Long id);

  CompletableFuture<Map<String,String>> deleteProblem(Long id);
}
