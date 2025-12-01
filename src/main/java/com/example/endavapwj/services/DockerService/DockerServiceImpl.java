package com.example.endavapwj.services.DockerService;

import com.example.endavapwj.DTOs.ContainerConfigDTO;
import com.example.endavapwj.DTOs.JudgeRequestMessageDTO;
import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.Submission;
import com.example.endavapwj.collection.TestCase;
import com.example.endavapwj.enums.Language;
import com.example.endavapwj.enums.Verdict;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.repositories.SubmissionRepository;
import com.example.endavapwj.repositories.TestCaseRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DockerServiceImpl implements DockerService {
  private final SubmissionRepository submissions;
  private final TestCaseRepository testCaseRepository;
  private final DockerClient dockerClient;
  private final SubmissionRepository submissionRepository;

  @Override
  public void executeSubmission(JudgeRequestMessageDTO msg)
      throws IOException, InterruptedException {

    Submission submission =
        submissions
            .findById(msg.getSubmissionId())
            .orElseThrow(() -> new NotFoundException("Submission not found"));

    Problem p = submission.getProblem();
    List<TestCase> tests = testCaseRepository.findByProblemId(p.getId());

    ContainerConfigDTO containerConfig = buildDockerRunConfig(submission.getLanguage());

    Path workDir = Files.createTempDirectory("submission-" + msg.getSubmissionId());
    Path sourceFile = workDir.resolve(containerConfig.getSourceFileName());
    Files.writeString(sourceFile, submission.getSource());
    Path inputFile = workDir.resolve(containerConfig.getInputFileName());
    String path = workDir.toAbsolutePath().toString();
    String image = containerConfig.getImage();
    String containerSource = containerConfig.getContainerSourceArg();
    String containerInput = containerConfig.getContainerInputArg();
    String timeLimitMs = msg.getTimeLimit().toString();
    String memoryLimitKb = msg.getMemoryLimit().toString();

    submission.setVerdict(Verdict.RUNNING);
    submissionRepository.save(submission);


    HostConfig hostConfig =
            HostConfig.newHostConfig()
                    .withBinds(new Bind(path, new Volume("/work")))
                    .withMemory((Integer.parseInt(memoryLimitKb) * 1024L))
                    .withMemorySwap((Integer.parseInt(memoryLimitKb) * 1024L))
                    .withCpuQuota(50000L)
                    .withNetworkMode("none");

    CreateContainerResponse compileContainer =
            dockerClient
                    .createContainerCmd(image)
                    .withHostConfig(hostConfig)
                    .withEntrypoint("/compile.sh")
                    .withCmd(containerSource, memoryLimitKb)
                    .exec();

    String compileContainerId = compileContainer.getId();
    ByteArrayOutputStream compileOutputStream = new ByteArrayOutputStream();

    try (ResultCallback.Adapter<Frame> callback =
                 dockerClient
                         .attachContainerCmd(compileContainerId)
                         .withStdOut(true)
                         .withStdErr(true)
                         .withFollowStream(true)
                         .exec(new ResultCallback.Adapter<>() {
                           @Override
                           public void onNext(Frame frame) {
                             try {
                               compileOutputStream.write(frame.getPayload());
                             } catch (IOException e) {
                               e.printStackTrace();
                             }
                           }
                         })) {

      dockerClient.startContainerCmd(compileContainerId).exec();

      int compileExitCode =
              dockerClient
                      .waitContainerCmd(compileContainerId)
                      .exec(new WaitContainerResultCallback())
                      .awaitStatusCode();

      callback.awaitCompletion();

      String compileOutput = compileOutputStream.toString(StandardCharsets.UTF_8);

      if (compileExitCode != 0 || compileOutput.startsWith("COMPILE_ERROR")) {
        submission.setVerdict(Verdict.CE);
        submission.setFinishedAt(Instant.now());
        submission.setOutput(compileOutput);
        submissionRepository.save(submission);
        return;
      }
    } finally {
      try {
        dockerClient.removeContainerCmd(compileContainerId).withForce(true).exec();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    for (TestCase test : tests) {
      Files.writeString(inputFile, test.getInput(), StandardCharsets.UTF_8);

      HostConfig runHostConfig =
              HostConfig.newHostConfig()
                      .withBinds(new Bind(path, new Volume("/work")))
                      .withMemory((Integer.parseInt(memoryLimitKb) * 1024L))
                      .withMemorySwap((Integer.parseInt(memoryLimitKb) * 1024L))
                      .withCpuQuota(50000L)
                      .withNetworkMode("none");

      CreateContainerResponse container =
              dockerClient
                      .createContainerCmd(image)
                      .withHostConfig(runHostConfig)
                      .withCmd(containerSource, containerInput, timeLimitMs, memoryLimitKb)
                      .exec();


      String containerId = container.getId();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      try (ResultCallback.Adapter<Frame> logCallback =
                   dockerClient
                           .attachContainerCmd(containerId)
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
                           })) {

        dockerClient.startContainerCmd(containerId).exec();

        int exitCode =
                dockerClient
                        .waitContainerCmd(containerId)
                        .exec(new WaitContainerResultCallback())
                        .awaitStatusCode();

        logCallback.awaitCompletion();

        String output = outputStream.toString(StandardCharsets.UTF_8);
        System.out.println("DEBUG "+submission.getLanguage()+" raw output: [" + output + "]");
        Verdict verdict = decideVerdict(exitCode, output, test.getOutput());
        if (verdict != Verdict.AC) {
          submission.setVerdict(verdict);
          submission.setFinishedAt(Instant.now());
          submission.setOutput(output);
          submission.setExpectedOutput(verdict == Verdict.WA ? test.getOutput() : "N/A");
          submissionRepository.save(submission);
          return;
        }
      } finally {
        try {
          dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    submission.setVerdict(Verdict.AC);
    submission.setFinishedAt(Instant.now());
    submissionRepository.save(submission);
  }

  private ContainerConfigDTO buildDockerRunConfig(Language language) {
    return switch (language) {
      case CPP ->
          new ContainerConfigDTO("cpp-runner", "main.cpp", "input.txt", "main.cpp", "input.txt");
      case PY ->
          new ContainerConfigDTO("py-runner", "main.py", "input.txt", "main.py", "input.txt");
      case JAVA ->
          new ContainerConfigDTO("java-runner", "Main.java", "input.txt", "Main.java", "input.txt");
    };
  }

  private Verdict decideVerdict(int containerExitCode, String output, String expectedOutput) {
    String trimmed = output.trim();

    if (trimmed.startsWith("COMPILE_ERROR")) {
      return Verdict.CE;
    }
    if (trimmed.startsWith("TIME_LIMIT_EXCEEDED")) {
      return Verdict.TLE;
    }
    if (trimmed.startsWith("RUNTIME_ERROR")) {
      return Verdict.RE;
    }

    if (containerExitCode == 137) {
      return Verdict.MLE;
    }
    if (containerExitCode == 134 || containerExitCode == 139) {
      return Verdict.RE;
    }
    if (containerExitCode == 124) {
      return Verdict.TLE;
    }
    if (containerExitCode == 0) {
      return trimmed.equals(expectedOutput.trim()) ? Verdict.AC : Verdict.WA;
    }

    return Verdict.RE;
  }
}
