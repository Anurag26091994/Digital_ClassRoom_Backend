package com.digital.repository;

import com.digital.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);


    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(@NotBlank(message = "Username is required") @Size(min = 4, max = 50, message = "Username must be between 4 to 50 characters") String username);
}
