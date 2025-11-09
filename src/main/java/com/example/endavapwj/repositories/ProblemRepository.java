package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
