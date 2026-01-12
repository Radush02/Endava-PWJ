package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    void deleteAllByCommentId(Long id);
}
