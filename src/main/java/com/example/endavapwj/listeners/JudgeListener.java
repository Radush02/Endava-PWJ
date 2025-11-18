package com.example.endavapwj.listeners;

import com.example.endavapwj.DTOs.JudgeRequestMessage;
import com.example.endavapwj.DTOs.JudgeResultMessage;
import com.example.endavapwj.collection.Submission;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.repositories.SubmissionRepository;
import com.example.endavapwj.util.JudgeQueueConfig;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.nio.file.*;

@Component
@RequiredArgsConstructor
public class JudgeListener {

  private final SubmissionRepository submissions;

  @RabbitListener(queues = JudgeQueueConfig.RESULT)
  @Transactional
  public void handleJudgeResult(JudgeResultMessage msg) {
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
  public void handleJudgeRequest(JudgeRequestMessage msg) throws IOException, InterruptedException {
      Path workDir = Files.createTempDirectory("submission-" + msg.getSubmissionId());
      Path source=workDir.resolve("main.cpp");
      Submission submission = submissions.findById(msg.getSubmissionId()).orElseThrow(()->new NotFoundException("Submission not found"));
      Files.writeString(source, submission.getSource());

      String image = "cpp-runner";
      String path=workDir.toAbsolutePath().toString();
      ProcessBuilder pb = new ProcessBuilder("docker","run","--rm",
              "-v",path,":/work",image,"/run.sh");

      pb.directory(workDir.toFile());
      pb.redirectErrorStream(true);

      Process process = pb.start();

      StringBuilder sb = new StringBuilder();
      try (var reader = new BufferedReader(
              new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
              sb.append(line).append("\n");
          }
      }

      int exitCode = process.waitFor();
      String output = sb.toString();

      System.out.println("Container exit code: " + exitCode);
      System.out.println("Container output:\n" + output);
  }
}
