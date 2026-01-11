package com.example.endavapwj.services.SubmissionService;

import com.example.endavapwj.DTOs.SubmissionDTO.SubmitCodeDTO;
import com.example.endavapwj.DTOs.DockerDTO.JudgeRequestMessageDTO;
import com.example.endavapwj.DTOs.ProblemDTO.ProblemDTO;
import com.example.endavapwj.DTOs.SubmissionDTO.SubmissionDTO;
import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.Submission;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.enums.Verdict;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.repositories.ProblemRepository;
import com.example.endavapwj.repositories.SubmissionRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JudgeQueueConfig;
import com.example.endavapwj.util.JwtUtil;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubmissionServiceImpl implements SubmissionService {
  private final SubmissionRepository submissionRepository;
  private final ProblemRepository problemRepository;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final RabbitTemplate rabbitTemplate;

  public SubmissionServiceImpl(
      SubmissionRepository submissionRepository,
      ProblemRepository problemRepository,
      UserRepository userRepository,
      JwtUtil jwtUtil,
      RabbitTemplate rabbitTemplate) {
    this.submissionRepository = submissionRepository;
    this.problemRepository = problemRepository;
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public CompletableFuture<Map<String, String>> createSubmission(SubmitCodeDTO submitCodeDTO) {
    Problem problem =
        problemRepository
            .findById(submitCodeDTO.getProblemId())
            .orElseThrow(() -> new NotFoundException("Problem not found"));

    Submission s = saveSubmissionBeforeSignalling(submitCodeDTO, problem);

    JudgeRequestMessageDTO judgeRequestMessage =
        JudgeRequestMessageDTO.builder()
            .submissionId(s.getId())
            .problemId(problem.getId())
            .memoryLimit(problem.getMemoryLimit())
            .timeLimit(problem.getTimeLimit())
            .build();

    rabbitTemplate.convertAndSend(
        JudgeQueueConfig.EXCHANGE, JudgeQueueConfig.ROUTING_KEY, judgeRequestMessage);

    return CompletableFuture.completedFuture(
        Map.of("message", "Submission pending.", "submissionId", s.getId()));
  }

  @Override
  public CompletableFuture<SubmissionDTO> getSubmission(String id) {
    User user =
        userRepository
            .findByUsername(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));

    Submission submission =
        submissionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Submission not found"));
    SubmissionDTO s =
        SubmissionDTO.builder()
            .username(user.getUsername())
            .submissionId(submission.getId())
            .problem(
                new ProblemDTO(
                    submission.getProblem().getTitle(), submission.getProblem().getDifficulty()))
            .verdict(submission.getVerdict())
            .language(submission.getLanguage())
            .output(submission.getOutput())
            .expectedOutput(submission.getExpectedOutput())
            .createdAt(submission.getCreatedAt())
            .finishedAt(submission.getFinishedAt())
            .build();
    return CompletableFuture.completedFuture(s);
  }

  protected Submission saveSubmissionBeforeSignalling(
      SubmitCodeDTO submitCodeDTO, Problem problem) {
    User user =
        userRepository
            .findByUsername(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));

    Submission s =
        Submission.builder()
            .problem(problem)
            .source(submitCodeDTO.getSource())
            .author(user)
            .verdict(Verdict.QUEUED)
            .createdAt(Instant.now())
            .language(submitCodeDTO.getLanguage())
            .build();

    return submissionRepository.save(s);
  }
}
