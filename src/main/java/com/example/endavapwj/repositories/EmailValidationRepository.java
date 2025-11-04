package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.EmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, String> {
    EmailValidation deleteEmailValidationByEmailHash(String emailHashKey);
    Boolean  existsByEmailHash(String emailHashKey);
}
