package com.example.endavapwj.services.TestCaseService;

import com.example.endavapwj.DTOs.TestCaseDTO.CreateTestCaseDTO;
import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.TestCase;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.repositories.ProblemRepository;
import com.example.endavapwj.repositories.TestCaseRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class TestCaseServiceImpl implements TestCaseService {

  private final TestCaseRepository testCaseRepository;
  private final UserRepository userRepository;
  private final ProblemRepository problemRepository;
  private final JwtUtil jwtUtil;

  public TestCaseServiceImpl(
      TestCaseRepository testCaseRepository,
      UserRepository userRepository,
      ProblemRepository problemRepository,
      JwtUtil jwtUtil) {
    this.testCaseRepository = testCaseRepository;
    this.userRepository = userRepository;
    this.problemRepository = problemRepository;
    this.jwtUtil = jwtUtil;
  }

  @Transactional
  @Override
  public CompletableFuture<Map<String, String>> addTestCase(CreateTestCaseDTO createTestCaseDTO) {
    User u =
        userRepository
            .findByUsername(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
    Problem p =
        problemRepository
            .findByTitle(createTestCaseDTO.getProblemTitle())
            .orElse(
                problemRepository
                    .findById(createTestCaseDTO.getProblemId())
                    .orElseThrow(() -> new NotFoundException("Problem not found")));

    if (!p.getAdmin().getUsername().equals(u.getUsername())) {
      throw new NotPermittedException("Only the author can add test cases");
    }

    TestCase tc =
        TestCase.builder()
            .input(createTestCaseDTO.getInput())
            .output(createTestCaseDTO.getOutput())
            .problem(p)
            .build();
    testCaseRepository.save(tc);

    return CompletableFuture.completedFuture(Map.of("message", "Test case included."));
  }
}
