package com.example.endavapwj.collection;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "emailValidation")
@Getter
@Setter
public class EmailValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    @Column(nullable=false)
    private String validationHash;
    @Column(nullable = false)
    @CreatedDate
    private Date createdAt;
}
