package com.example.endavapwj.collection;


import com.example.endavapwj.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false,length = 30)
    @NotEmpty(message="Username cannot be empty")
    private String username;

    @Column(unique = true,nullable=false)
    @NotEmpty(message="Email cannot be empty")
    @Pattern(regexp = ".+@.+\\..+",message="Email is invalid")
    private String email;

    @Column(nullable=false)
    @NotEmpty(message="Password cannot be empty")
    private String password;

    @Column
    private String fullName;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "admin",cascade = CascadeType.ALL)
    private Set<Problem> problems;

}
