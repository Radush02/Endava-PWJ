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
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeListener {

  private final SubmissionRepository submissions;
  private final TestCaseRepository testCaseRepository;
  private final DockerClient dockerClient;

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

    Problem p = submission.getProblem();
    List<TestCase> tests = testCaseRepository.findByProblemId(p.getId());

    System.out.println("ok");
    String image = "cpp-runner";
    String path = workDir.toAbsolutePath().toString();

    String containerSource = "main.cpp";
    String containerInput = "input.txt";
    String timeLimitMs = msg.getTimeLimit().toString();
    String memoryLimitKb = msg.getMemoryLimit().toString();


    for (TestCase test : tests) {
      Files.writeString(inputFile, test.getInput(), StandardCharsets.UTF_8);

      HostConfig hostConfig = HostConfig.newHostConfig()
              .withBinds(new Bind(path, new Volume("/work")));

      CreateContainerResponse container = dockerClient.createContainerCmd(image)
              .withHostConfig(hostConfig)
              .withCmd(containerSource, containerInput, timeLimitMs, memoryLimitKb)
              .exec();

      System.out.println("container created");
      String containerId = container.getId();

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      //https://stackoverflow.com/questions/70005729/accessing-the-output-of-a-command-running-in-a-docker-container
      //de reverificat
      try (
              ResultCallback.Adapter<Frame> logCallback =
                      dockerClient.attachContainerCmd(containerId)
                              .withStdOut(true)
                              .withStdErr(true)
                              .withFollowStream(true)
                              .exec(new ResultCallback.Adapter<>() {
                                @Override
                                public void onNext(Frame frame) {
                                  try {
                                    outputStream.write(frame.getPayload());
                                  } catch (IOException e) {
                                    e.printStackTrace();
                                  }
                                }
                              })

      ) {
        dockerClient.startContainerCmd(containerId).exec();

        int exitCode = dockerClient.waitContainerCmd(containerId)
                .exec(new WaitContainerResultCallback())
                .awaitStatusCode();

        logCallback.awaitCompletion();

        String output = outputStream.toString(StandardCharsets.UTF_8);

        System.out.println("Container exit code: " + exitCode);
        System.out.println("Container output:\n" + output);
        System.out.println("Expected output:\n" + test.getOutput());
        System.out.println("Matches?\n" + output.trim().equals(test.getOutput().trim()));

      } finally {
        try {
          dockerClient.removeContainerCmd(containerId)
                  .withForce(true)
                  .exec();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
