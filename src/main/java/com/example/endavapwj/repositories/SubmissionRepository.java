package com.example.endavapwj.repositories;

import com.example.endavapwj.DTOs.SubmissionDTO.BestSubmissionDTO;
import com.example.endavapwj.DTOs.SubmissionDTO.LeaderboardDTO;
import com.example.endavapwj.collection.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    @Query("SELECT new com.example.endavapwj.DTOs.SubmissionDTO.LeaderboardDTO(" +
            "s.author.username, COUNT(DISTINCT s.problem.id)) " +
            "FROM Submission s " +
            "WHERE s.verdict = 'ACCEPTED' " +
            "GROUP BY s.author.username " +
            "ORDER BY COUNT(DISTINCT s.problem.id) DESC")
    List<LeaderboardDTO> getLeaderboard();


    @Query("SELECT s " +
            "FROM Submission s " +
            "WHERE s.problem.id = :problemId AND s.verdict = com.example.endavapwj.enums.Verdict.AC " +
            "ORDER BY s.finishedAt - s.createdAt ASC")
    List<Submission> findBestSubmissionsByProblem(Long problemId);

}
