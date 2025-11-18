package com.example.endavapwj.collection;

import com.example.endavapwj.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 30)
  @NotEmpty(message = "Username cannot be empty")
  @Pattern(
      regexp = "^[a-zA-Z0-9._-]{3,20}$",
      message =
          "Username contains invalid characters or is of invalid length (more than 3 and less than 20 characters).")
  private String username;

  @Column(unique = true, nullable = false)
  @NotEmpty(message = "Email cannot be empty")
  @Pattern(regexp = ".+@.+\\..+", message = "Email is invalid")
  private String email;

  @Column(nullable = false)
  @Size(min = 8, max = 128)
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
      message =
          "Password must include at least one lowercase, one uppercase, one digit and one special character.")
  private String password;

  @Column() private String fullName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column private Date emailVerifiedAt;

  @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
  private Set<Problem> problems;
}
