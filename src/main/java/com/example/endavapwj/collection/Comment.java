package com.example.endavapwj.collection;

import com.example.endavapwj.enums.CommentVoteType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comment")
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne(optional = false)
  private Problem problem;

  @Lob
  private String comment;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<CommentVote> votes = new HashSet<>();

  public int countUpvotes() {
    return (int) this.getVotes().stream()
            .filter(v -> v.getType() == CommentVoteType.UPVOTE)
            .count();
  }
  public int countDownvotes() {
    return (int) this.getVotes().stream()
            .filter(v -> v.getType() == CommentVoteType.DOWNVOTE)
            .count();
  }
}
