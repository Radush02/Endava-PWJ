package com.example.endavapwj.controllers;

import com.example.endavapwj.collection.EmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, String> {
    EmailValidation deleteEmailValidationByEmail(String email);
}
