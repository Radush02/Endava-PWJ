package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.TestCase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
  List<TestCase> findByProblemId(Long problemId);
}
