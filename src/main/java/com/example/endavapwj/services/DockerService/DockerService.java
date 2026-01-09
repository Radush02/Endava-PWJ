package com.example.endavapwj.services.DockerService;

import com.example.endavapwj.DTOs.DockerDTO.JudgeRequestMessageDTO;
import com.example.endavapwj.DTOs.SubmissionDTO.SubmissionDTO;
import java.io.IOException;

public interface DockerService {

  SubmissionDTO executeSubmission(JudgeRequestMessageDTO msg)
      throws IOException, InterruptedException;
}
