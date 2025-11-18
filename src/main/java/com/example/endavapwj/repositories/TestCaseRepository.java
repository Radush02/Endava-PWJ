package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.TestCase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, String> {
  List<TestCase> findByProblemId(Long problemId);
}
