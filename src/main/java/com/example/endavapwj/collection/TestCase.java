package com.example.endavapwj.collection;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="testcase")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="problem_id",nullable = false)
    private Problem problem;

    @NotBlank(message="Define the input.")
    private String input;

    @NotBlank(message="Define the output.")
    private String output;

}
