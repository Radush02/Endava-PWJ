package com.example.endavapwj.listeners;

import com.example.endavapwj.DTOs.DockerDTO.JudgeRequestMessageDTO;
import com.example.endavapwj.DTOs.SubmissionDTO.SubmissionDTO;
import com.example.endavapwj.repositories.SubmissionRepository;
import com.example.endavapwj.services.DockerService.DockerService;
import com.example.endavapwj.util.JudgeQueueConfig;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeListener {

  private final SubmissionRepository submissionRepository;
  private final DockerService dockerService;
  private final RabbitTemplate rabbitTemplate;

  // nu cred ca e nev sa dau update si dupa, dar in fine
  @RabbitListener(queues = JudgeQueueConfig.RESULT)
  public void handleJudgeResult(SubmissionDTO msg) {
    submissionRepository
        .findById(msg.getSubmissionId())
        .ifPresent(
            sub -> {
              sub.setVerdict(msg.getVerdict());
              sub.setFinishedAt(Instant.now());
              submissionRepository.save(sub);
            });
  }

  @RabbitListener(queues = JudgeQueueConfig.QUEUE)
  public void handleJudgeRequest(JudgeRequestMessageDTO msg)
      throws IOException, InterruptedException {
    SubmissionDTO result = dockerService.executeSubmission(msg);

    rabbitTemplate.convertAndSend(
        JudgeQueueConfig.EXCHANGE, JudgeQueueConfig.RESULT_ROUTING_KEY, result);
  }


}
