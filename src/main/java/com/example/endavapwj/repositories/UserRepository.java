package com.example.endavapwj.repositories;

import com.example.endavapwj.DTOs.UserDTO.UserTopDTO;
import com.example.endavapwj.collection.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Boolean existsByUsernameOrEmailIgnoreCase(String username, String email);

  Boolean existsByEmail(String email);

  Optional<User> findByUsernameIgnoreCase(String username);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmailIgnoreCase(String email);

  @Query("SELECT new com.example.endavapwj.DTOs.UserDTO.UserTopDTO(" +
          "s.author.username, " +
          "COUNT(DISTINCT s.problem.id), " +
          "SUM(CASE WHEN s.problem.difficulty = 'EASY' THEN 1 ELSE 0 END), " +
          "SUM(CASE WHEN s.problem.difficulty = 'MEDIUM' THEN 1 ELSE 0 END), " +
          "SUM(CASE WHEN s.problem.difficulty = 'HARD' THEN 1 ELSE 0 END)) " +
          "FROM Submission s " +
          "WHERE s.verdict = 'ACCEPTED' " +
          "GROUP BY s.author.username " +
          "ORDER BY COUNT(DISTINCT s.problem.id) DESC")
  List<UserTopDTO> getAdvancedLeaderboard();
}
