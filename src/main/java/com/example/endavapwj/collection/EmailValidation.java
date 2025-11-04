package com.example.endavapwj.collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emailValidation")
@Getter
@Setter
public class EmailValidation {

    @Id
    private String email;
    @Column(nullable=false)
    private String emailHash;
}
