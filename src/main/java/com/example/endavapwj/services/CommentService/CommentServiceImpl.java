package com.example.endavapwj.services.CommentService;

import com.example.endavapwj.DTOs.CommentDTO.AddCommentDTO;
import com.example.endavapwj.DTOs.CommentDTO.AddCommentVoteDTO;
import com.example.endavapwj.collection.Comment;
import com.example.endavapwj.collection.CommentVote;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.NotFoundException;
import com.example.endavapwj.repositories.CommentRepository;
import com.example.endavapwj.repositories.CommentVoteRepository;
import com.example.endavapwj.repositories.ProblemRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JwtUtil;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final ProblemRepository problemRepository;
  private final CommentVoteRepository commentVoteRepository;
  private final JwtUtil jwtUtil;

  @Override
  public CompletableFuture<Map<String, String>> addComment(AddCommentDTO commentDTO) {
    User u =
        userRepository
            .findByUsernameIgnoreCase(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
    Comment c =
        Comment.builder()
            .comment(commentDTO.getComment())
            .user(u)
            .problem(
                problemRepository
                    .findById(commentDTO.getProblemId())
                    .orElseThrow(() -> new NotFoundException("Problem not found")))
            .build();
    commentRepository.save(c);
    return CompletableFuture.completedFuture(Map.of("comment", c.getComment()));
  }

  @Override
  public CompletableFuture<Map<String, String>> addVote(AddCommentVoteDTO voteDTO) {
    User u =
        userRepository
            .findByUsernameIgnoreCase(jwtUtil.extractUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
    Comment c =
        commentRepository
            .findById(voteDTO.getCommentId())
            .orElseThrow(() -> new NotFoundException("Comment not found"));
    try {
      CommentVote cv = CommentVote.builder().comment(c).user(u).type(voteDTO.getType()).build();
      commentVoteRepository.save(cv);
      return CompletableFuture.completedFuture(Map.of("voteType", cv.getType().toString()));
    } catch (DataIntegrityViolationException ex) {
      // sau sa scot votu? :hmm:
      throw new AlreadyExistsException("You already voted.");
    }
  }
}
