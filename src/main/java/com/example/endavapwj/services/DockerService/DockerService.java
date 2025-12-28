package com.example.endavapwj.services.DockerService;

import com.example.endavapwj.DTOs.DockerDTO.JudgeRequestMessageDTO;
import java.io.IOException;

public interface DockerService {

  void executeSubmission(JudgeRequestMessageDTO msg) throws IOException, InterruptedException;
}
