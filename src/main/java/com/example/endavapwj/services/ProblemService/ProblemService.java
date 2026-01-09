package com.example.endavapwj.services.ProblemService;

import com.example.endavapwj.DTOs.ProblemDTO.EditProblemDTO;
import com.example.endavapwj.DTOs.ProblemDTO.FullProblemDTO;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ProblemService {

  @Transactional
  CompletableFuture<Map<String, String>> create(EditProblemDTO.CreateProblemDTO createProblemDTO);

  @Transactional
  CompletableFuture<Map<String, String>> edit(EditProblemDTO editProblemDTO);

  @Transactional
  CompletableFuture<Map<String, String>> delete(String title);

  CompletableFuture<FullProblemDTO> getById(Long id);
}
