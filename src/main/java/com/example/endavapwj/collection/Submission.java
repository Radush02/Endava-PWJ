package com.example.endavapwj.collection;

import com.example.endavapwj.enums.Language;
import com.example.endavapwj.enums.Verdict;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "submission")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Submission {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(optional = false)
  private Problem problem;

  @ManyToOne(optional = false)
  private User author;

  @Lob private String source;

  @Enumerated(EnumType.STRING)
  private Verdict verdict;

  @Column(nullable = false)
  private Language language;

  private String output;
  private String expectedOutput;
  private Instant createdAt;
  private Instant finishedAt;
}
