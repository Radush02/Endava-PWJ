package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Boolean existsByUsernameOrEmailIgnoreCase(String username, String email);

  Boolean existsByEmail(String email);

  Optional<User> findByUsernameIgnoreCase(String username);

  Optional<User> findByUsername(String username);
}
