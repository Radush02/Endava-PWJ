package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.EmailValidation;
import com.example.endavapwj.collection.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, String> {
  Optional<EmailValidation> findByValidationHash(String hash);

  void deleteByUser(User user);
}
