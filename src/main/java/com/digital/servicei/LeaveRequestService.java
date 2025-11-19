package com.digital.servicei;

import com.digital.dto.LeaveRequestDto;
import com.digital.dto.MakeLeaveRequest;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequestDto applyForLeave(MakeLeaveRequest makeLeaveRequest, String username);

    List<LeaveRequestDto> viewLeaveApprovalStatus(String username);

    LeaveRequestDto approveStudentLeaveRequest(String username, Long leaveRequestId);

    LeaveRequestDto rejectStudentLeaveRequest(String username, Long leaveRequestId, String remarks);

    List<LeaveRequestDto> viewAllPendingLeaveRequests();

    LeaveRequestDto approveTeacherLeaveRequest(Long leaveRequestId);

    LeaveRequestDto rejectTeacherLeaveRequest(Long leaveRequestId, String remarks);

    List<LeaveRequestDto> viewStudentPendingLeaveRequests(String username);
}
