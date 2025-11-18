package com.example.endavapwj.collection;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "testcase")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestCase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  @NotBlank(message = "Define the input.")
  private String input;

  @NotBlank(message = "Define the output.")
  private String output;
}
