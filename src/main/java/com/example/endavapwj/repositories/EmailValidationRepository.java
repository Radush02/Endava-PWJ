package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.EmailValidation;
import com.example.endavapwj.collection.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, String> {
    Optional<EmailValidation> findByValidationHash(String hash);
    void deleteByUser(User user);
}
