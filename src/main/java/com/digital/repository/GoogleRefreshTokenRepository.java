package com.digital.repository;

import com.digital.google_meet_config.GoogleRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleRefreshTokenRepository extends JpaRepository<GoogleRefreshToken, Long> {
    Optional<GoogleRefreshToken> findByTeacher_Id(Long teacherId);


}
