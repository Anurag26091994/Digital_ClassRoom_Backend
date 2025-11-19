package com.digital.repository;

import com.digital.entity.LeaveRequest;
import com.digital.entity.Teacher;
import com.digital.enums.LeaveRequestStatus;
import com.digital.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findAllByUser_UserIdAndStatus(Long userId, LeaveRequestStatus leaveRequestStatus);

    List<LeaveRequest> findAllByApprovedByAdmin_Role(Role role);

    List<LeaveRequest> findAllByApprovedByTeacherAndStatus(Teacher teacher, LeaveRequestStatus leaveRequestStatus);

    List<LeaveRequest> findAllByStatus(LeaveRequestStatus leaveRequestStatus);

    boolean existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(Long userId, LocalDate fromDate, LocalDate toDate);

    List<LeaveRequest> findAllByApprovedByAdmin_RoleAndStatus(Role role, LeaveRequestStatus leaveRequestStatus);
}
