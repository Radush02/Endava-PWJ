package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.Problem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
  Optional<Problem> findByTitleIgnoreCase(String title);

  Optional<Problem> findById(Long id);
}
