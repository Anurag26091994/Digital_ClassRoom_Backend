package com.digital.repository;

import com.digital.entity.LeaveAttendanceSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveAttendanceSyncRepository extends JpaRepository<LeaveAttendanceSync, Long> {
    List<LeaveAttendanceSync> findAllByLeaveRequest_LeaveId(Long leaveId);
}
