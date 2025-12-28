package com.example.endavapwj.listeners;

import com.example.endavapwj.DTOs.DockerDTO.JudgeRequestMessageDTO;
import com.example.endavapwj.DTOs.DockerDTO.JudgeResultMessageDTO;
import com.example.endavapwj.repositories.SubmissionRepository;
import com.example.endavapwj.services.DockerService.DockerService;
import com.example.endavapwj.util.JudgeQueueConfig;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeListener {

  private final SubmissionRepository submissions;
  private final DockerService dockerService;

  @RabbitListener(queues = JudgeQueueConfig.RESULT)
  public void handleJudgeResult(JudgeResultMessageDTO msg) {
    submissions
        .findById(msg.getSubmissionId())
        .ifPresent(
            sub -> {
              sub.setVerdict(msg.getVerdict());
              sub.setFinishedAt(Instant.now());
              submissions.save(sub);
            });
  }

  @RabbitListener(queues = JudgeQueueConfig.QUEUE)
  public void handleJudgeRequest(JudgeRequestMessageDTO msg)
      throws IOException, InterruptedException {
    dockerService.executeSubmission(msg);
  }
}
