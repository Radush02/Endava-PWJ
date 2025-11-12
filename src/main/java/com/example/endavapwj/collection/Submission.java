package com.example.endavapwj.collection;

import com.example.endavapwj.enums.Verdict;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="submission")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Submission {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional=false)
    private Problem problem;

    @ManyToOne(optional=false)
    private User author;

    @Lob
    private String source;

    @Enumerated(EnumType.STRING)
    private Verdict verdict;

    private Integer maxTimeMs;
    private Integer maxMemoryKb;

    private Instant createdAt;
    private Instant finishedAt;
}
