package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.Problem;

import java.util.List;
import java.util.Optional;

import com.example.endavapwj.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
  Optional<Problem> findByTitleIgnoreCase(String title);

  Optional<Problem> findById(Long id);

  @Query("SELECT p FROM Problem p WHERE " +
          "(:difficulty IS NULL OR p.difficulty = :difficulty) AND " +
          "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))")
  List<Problem> findByDifficultyAndTitle(
          Difficulty difficulty,
          String search
  );
}
