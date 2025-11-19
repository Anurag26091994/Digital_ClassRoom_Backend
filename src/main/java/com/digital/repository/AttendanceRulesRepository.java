package com.digital.repository;

import com.digital.entity.AttendanceRule;
import com.digital.enums.AttendanceRuleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRulesRepository extends JpaRepository<AttendanceRule, Long> {

    Optional<AttendanceRule> findByRuleName(AttendanceRuleName ruleName);
}
