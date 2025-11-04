package com.example.endavapwj.repositories;

import com.example.endavapwj.collection.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsernameOrEmail(String username, String email);
    Boolean existsByEmail(String email);
}
