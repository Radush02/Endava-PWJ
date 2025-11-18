package com.example.endavapwj.listeners;

import com.example.endavapwj.DTOs.JudgeRequestMessageDTO;
import com.example.endavapwj.DTOs.JudgeResultMessageDTO;
import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.Submission;
import com.example.endavapwj.collection.TestCase;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.repositories.SubmissionRepository;
import com.example.endavapwj.repositories.TestCaseRepository;
import com.example.endavapwj.util.JudgeQueueConfig;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeListener {

  private final SubmissionRepository submissions;
  private final TestCaseRepository testCaseRepository;

  @RabbitListener(queues = JudgeQueueConfig.RESULT)
  @Transactional
  public void handleJudgeResult(JudgeResultMessageDTO msg) {
    submissions
        .findById(msg.getSubmissionId())
        .ifPresent(
            sub -> {
              sub.setVerdict(msg.getVerdict());
              sub.setMaxTimeMs(msg.getMaxTimeMs());
              sub.setMaxMemoryKb(msg.getMaxMemoryKb());
              sub.setFinishedAt(Instant.now());
              submissions.save(sub);
            });
  }

  @RabbitListener(queues = JudgeQueueConfig.QUEUE)
  @Transactional
  public void handleJudgeRequest(JudgeRequestMessageDTO msg) throws IOException, InterruptedException {
    Path workDir = Files.createTempDirectory("submission-" + msg.getSubmissionId());
    Path source = workDir.resolve("main.cpp");

    Submission submission =
            submissions
                    .findById(msg.getSubmissionId())
                    .orElseThrow(() -> new NotFoundException("Submission not found"));

    Files.writeString(source, submission.getSource());


      Path inputFile = workDir.resolve("input.txt");
//    String input = "1 2\n";
//    Files.writeString(inputFile, input);

    Problem p = submission.getProblem();
    List<TestCase> tests = testCaseRepository.findByProblemId(p.getId());


    String image = "cpp-runner";
    String path = workDir.toAbsolutePath().toString();

    String containerSource = "main.cpp";
    String containerInput = "input.txt";
    String timeLimitMs = msg.getTimeLimit().toString();
    String memoryLimitKb = msg.getMemoryLimit().toString();

    for (TestCase test : tests) {
      Files.writeString(inputFile,test.getInput(), StandardCharsets.UTF_8);
      ProcessBuilder pb =
              new ProcessBuilder(
                      "docker",
                      "run",
                      "--rm",
                      "-v",
                      path + ":/work",
                      image,
                      containerSource,
                      containerInput,
                      timeLimitMs,
                      memoryLimitKb);

      pb.directory(workDir.toFile());
      pb.redirectErrorStream(true);

      Process process = pb.start();

      String output;
      try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        output = reader.lines().collect(Collectors.joining("\n"));
      }

      int exitCode = process.waitFor();

      System.out.println("Container exit code: " + exitCode);
      System.out.println("Container output:\n" + output);
      System.out.println("Expected output:\n"+test.getOutput());
      System.out.println("Matches?\n"+output.trim().equals(test.getOutput().trim()));
    }
  }
}
