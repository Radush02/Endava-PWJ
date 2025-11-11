package com.example.endavapwj.collection;

import com.example.endavapwj.enums.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name="problem")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255,unique=true)
    @NotBlank(message = "Title can't be blank")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Must include a description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Difficulty difficulty;

    @Column(nullable = false)
    @NotNull @Positive(message = "Set a time limit (positive)")
    private Integer timeLimit;

    @Column(nullable = false)
    @NotNull
    @Positive(message = "Set a memory limit in KB (positive)")
    private Integer memoryLimit;

    @ManyToOne
    @JoinColumn(name="admin_id", nullable=false)
    @NotNull
    private User admin;
}