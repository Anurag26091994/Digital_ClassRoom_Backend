package com.digital.repository;

import com.digital.entity.LibraryMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Long> {

    Optional<LibraryMember> findByUserId(Long userId);

    // You can add more query methods if needed
}
