package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
