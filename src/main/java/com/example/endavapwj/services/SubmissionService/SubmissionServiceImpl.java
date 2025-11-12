package com.example.endavapwj.services.SubmissionService;


import com.example.endavapwj.DTOs.JudgeRequestMessage;
import com.example.endavapwj.DTOs.SubmitCodeDTO;
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
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, ProblemRepository problemRepository, UserRepository userRepository, JwtUtil jwtUtil, RabbitTemplate rabbitTemplate) {
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    @Override
    public CompletableFuture<Map<String,String>> createSubmission(SubmitCodeDTO submitCodeDTO) {
        User user = userRepository.findByUsername(jwtUtil.extractUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Problem problem = problemRepository.findById(submitCodeDTO.getProblemId())
                .orElseThrow(() -> new NotFoundException("Problem not found"));

        Submission s = Submission.builder()
        .problem(problem)
        .source(submitCodeDTO.getSource())
        .author(user)
        .verdict(Verdict.QUEUED)
        .createdAt(Instant.now())
        .build();

        submissionRepository.save(s);

        JudgeRequestMessage judgeRequestMessage = JudgeRequestMessage
                .builder()
                .submissionId(s.getId())
                .problemId(problem.getId())
                .memoryLimit(problem.getMemoryLimit())
                .timeLimit(problem.getTimeLimit())
                .build();

        rabbitTemplate.convertAndSend(JudgeQueueConfig.QUEUE, judgeRequestMessage);
        return CompletableFuture.completedFuture(Map.of("message","Submission pending."));
    }
}
