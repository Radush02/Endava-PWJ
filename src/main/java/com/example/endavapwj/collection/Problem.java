package com.example.endavapwj.collection;

import com.example.endavapwj.enums.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty(message = "Title can't be null")
    private String title;

    @Column
    @NotEmpty(message = "Must include a description")
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column
    private Integer timeLimit;

    @Column
    private Integer memoryLimit;

    @ManyToOne
    @JoinColumn(name="admin_id", nullable=false)
    private User admin;
}
