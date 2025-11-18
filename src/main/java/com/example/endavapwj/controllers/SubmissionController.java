package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.DockerTestRequestDTO;
import com.example.endavapwj.DTOs.SubmitCodeDTO;
import com.example.endavapwj.services.SubmissionService.SubmissionService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/submissions")
public class SubmissionController {
  private final SubmissionService submissionService;

  public SubmissionController(SubmissionService submissionService) {
    this.submissionService = submissionService;
  }

  @PostMapping("/submit")
  public CompletableFuture<ResponseEntity<Map<String, String>>> submit(
      @RequestBody SubmitCodeDTO submitCodeDTO) {
    return submissionService
        .createSubmission(submitCodeDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.ACCEPTED).body(body));
  }

  @PostMapping("/test-docker")
  public ResponseEntity<String> testDocker(@RequestBody DockerTestRequestDTO dockerTestRequestDTO)
      throws IOException, InterruptedException {

    Path workDir = Files.createTempDirectory("docker-test-");
    Path sourceFile = workDir.resolve("main.cpp");
    Path inputFile = workDir.resolve("input.txt");

    Files.writeString(sourceFile, dockerTestRequestDTO.getSource(), StandardCharsets.UTF_8);
    Files.writeString(inputFile, "", StandardCharsets.UTF_8);

    String image = "cpp-runner";
    String workDirPath = workDir.toAbsolutePath().toString();

    ProcessBuilder pb =
        new ProcessBuilder(
            "docker",
            "run",
            "--rm",
            "-v",
            workDirPath + ":/work",
            image,
            "main.cpp",
            "input.txt",
            "2000",
            "262144");

    pb.directory(workDir.toFile());
    pb.redirectErrorStream(true);

    Process process = pb.start();

    String output;
    try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      output = reader.lines().collect(Collectors.joining("\n"));
    }

    int exitCode = process.waitFor();

    String responseBody =
        """
                Docker exit code: %d

                Script output:
                %s
                """
            .formatted(exitCode, output);

    return ResponseEntity.ok(responseBody);
  }
}
