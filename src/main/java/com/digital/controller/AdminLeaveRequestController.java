package com.digital.controller;

import com.digital.dto.LeaveRequestDto;
import com.digital.servicei.LeaveRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/admin/leave-requests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * ADMIN api to view all teacher's pending leave requests.
     * */

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<LeaveRequestDto>> viewAllPendingLeaveRequests(){

        return new ResponseEntity<List<LeaveRequestDto>>(leaveRequestService.viewAllPendingLeaveRequests(),
                HttpStatus.OK);
    }

    /**
     * ADMIN api to approve teacher's pending leave requests.
     * */

    @PutMapping(value = "/{leaveRequestId}/approve")
    public ResponseEntity<LeaveRequestDto> approveTeacherLeaveRequest(@PathVariable Long leaveRequestId){

        return new ResponseEntity<LeaveRequestDto>(leaveRequestService.approveTeacherLeaveRequest(leaveRequestId),
                HttpStatus.OK);
    }

    /**
     * ADMIN api to reject teacher's pending leave requests.
     * */

    @PutMapping(value = "/{leaveRequestId}/reject")
    public ResponseEntity<LeaveRequestDto> rejectTeacherLeaveRequest(@PathVariable Long leaveRequestId, @RequestParam String remarks){

        return new ResponseEntity<LeaveRequestDto>(leaveRequestService.rejectTeacherLeaveRequest(leaveRequestId, remarks),
                HttpStatus.OK);
    }
}
