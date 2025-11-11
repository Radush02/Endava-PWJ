package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.Problem;
import com.example.endavapwj.collection.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, String> {
    List<TestCase> findByProblemId(Long problemId);
}
