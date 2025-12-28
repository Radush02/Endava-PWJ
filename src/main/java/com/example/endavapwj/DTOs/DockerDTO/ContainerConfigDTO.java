package com.example.endavapwj.DTOs.DockerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContainerConfigDTO {
  String image;
  String sourceFileName;
  String inputFileName;
  String containerSourceArg;
  String containerInputArg;
}
