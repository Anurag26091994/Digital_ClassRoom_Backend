package com.digital.repository;

import com.digital.entity.Attendance;
import com.digital.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudent_StudentRegIdAndSession_SessionId(Long studentRegId, Long sessionId);

    Boolean existsByStudent_StudentRegIdAndSession_SessionId(Long studentRegId, Long sessionId);

    List<Attendance> findAllBySession_SessionId(Long sessionId);

    List<Attendance> findAllByDateBetween(LocalDate fromDate, LocalDate toDate);

    // Count days by student + status + date range
    @Query("SELECT COUNT(a) FROM Attendance a " +
            "WHERE a.student.studentRegId = :studentId " +
            "AND a.status = :status " +
            "AND a.date BETWEEN :startDate AND :endDate")
    Long countDaysByStatus(@Param("studentId") Long studentId,
                           @Param("status") AttendanceStatus status,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a " +
            "WHERE a.student.studentRegId = :studentId " +
            "AND a.date BETWEEN :startDate AND :endDate")
    Long countTotalDays(@Param("studentId") Long studentId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

    List<Attendance> findByStudent_StudentRegId(Long studentRegId);
}
