package com.example.endavapwj.services.ProblemService;

import com.example.endavapwj.DTOs.CommentDTO.CommentDTO;
import com.example.endavapwj.DTOs.ProblemDTO.EditProblemDTO;
import com.example.endavapwj.DTOs.ProblemDTO.FullProblemDTO;
import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.repositories.ProblemRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JwtUtil;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProblemServiceImpl implements ProblemService {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final ProblemRepository problemRepository;

  public ProblemServiceImpl(
      UserRepository userRepository, JwtUtil jwtUtil, ProblemRepository problemRepository) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
    this.problemRepository = problemRepository;
  }

  @Override
  @Transactional
  public CompletableFuture<Map<String, String>> create(
      EditProblemDTO.CreateProblemDTO createProblemDTO) {
    User u =
        this.userRepository
            .findByUsername(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
    if (u.getRole() != Role.Admin)
      throw new NotPermittedException("You do not have permission to perform this operation");
    Problem problem =
        Problem.builder()
            .title(createProblemDTO.getTitle())
            .description(createProblemDTO.getDescription())
            .difficulty(createProblemDTO.getDifficulty())
            .timeLimit(createProblemDTO.getTimeLimit())
            .memoryLimit(createProblemDTO.getMemoryLimit())
            .admin(u)
            .build();

    problemRepository.save(problem);
    return CompletableFuture.completedFuture(Map.of("message", "Problem created successfully"));
  }

  @Transactional
  @Override
  public CompletableFuture<Map<String, String>> edit(EditProblemDTO editProblemDTO) {
    User u =
        this.userRepository
            .findByUsername(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
    if (u.getRole() != Role.Admin)
      throw new NotPermittedException("You do not have permission to perform this operation");
    Problem problem =
        this.problemRepository
            .findByTitle(editProblemDTO.getTitle())
            .orElseThrow(() -> new NotFoundException("Problem not found"));
    problem.setDescription(
        editProblemDTO.getDescription() != null
            ? editProblemDTO.getDescription()
            : problem.getDescription());
    problem.setDifficulty(
        editProblemDTO.getDifficulty() != null
            ? editProblemDTO.getDifficulty()
            : problem.getDifficulty());
    problem.setTimeLimit(
        editProblemDTO.getTimeLimit() != 0
            ? editProblemDTO.getTimeLimit()
            : problem.getTimeLimit());
    problem.setMemoryLimit(
        editProblemDTO.getMemoryLimit() != 0
            ? editProblemDTO.getMemoryLimit()
            : problem.getMemoryLimit());

    this.problemRepository.save(problem);
    return CompletableFuture.completedFuture(Map.of("message", "Problem edited successfully"));
  }

  @Transactional
  @Override
  public CompletableFuture<Map<String, String>> delete(String title) {
    User u =
        this.userRepository
            .findByUsername(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
    if (u.getRole() != Role.Admin)
      throw new NotPermittedException("You do not have permission to perform this operation");
    Problem problem =
        this.problemRepository
            .findByTitle(title)
            .orElseThrow(() -> new NotFoundException("Problem not found"));
    problemRepository.delete(problem);

    return CompletableFuture.completedFuture(Map.of("message", "Problem deleted successfully"));
  }

  @Override
  public CompletableFuture<List<FullProblemDTO>> getAllProblems(int page,int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    Page<Problem> problemsPage = problemRepository.findAll(pageRequest);

    List<FullProblemDTO> dtoList = problemsPage
            .getContent()
            .stream()
            .map(this::mapProblemToFullDTO)
            .toList();

    Page<FullProblemDTO> dtoPage =
            new PageImpl<>(dtoList, problemsPage.getPageable(), problemsPage.getTotalElements());

    return CompletableFuture.completedFuture(dtoPage.stream().toList());
  }


  @Override
  public CompletableFuture<FullProblemDTO> getById(Long id) {
    Problem p = this.problemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Problem not found"));

    FullProblemDTO fp = mapProblemToFullDTO(p);

    return CompletableFuture.completedFuture(fp);
  }

  private FullProblemDTO mapProblemToFullDTO(Problem p) {
    FullProblemDTO fp = FullProblemDTO.builder()
            .id(p.getId())
            .title(p.getTitle())
            .description(p.getDescription())
            .difficulty(p.getDifficulty())
            .timeLimit(p.getTimeLimit())
            .memoryLimit(p.getMemoryLimit())
            .author(p.getAdmin().getUsername())
            .build();

    List<CommentDTO> comments = p.getComments().stream()
            .map(comm -> new CommentDTO(
                    comm.getUser().getUsername(),
                    comm.getComment(),
                    comm.countUpvotes(),
                    comm.countDownvotes()
            ))
            .toList();
    fp.setComments(comments);

    return fp;
  }

}


