package com.digital.controller;

import com.digital.dto.LeaveRequestDto;
import com.digital.dto.MakeLeaveRequest;
import com.digital.servicei.LeaveRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/teacher")
public class TeacherLeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * TEACHER api to apply for leave.
     * */

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping(value = "/leaves/apply", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LeaveRequestDto> applyForLeave(@RequestBody MakeLeaveRequest makeLeaveRequest){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<LeaveRequestDto>(leaveRequestService.applyForLeave(makeLeaveRequest, username),
                HttpStatus.CREATED);
    }

    /**
     * TEACHER and ADMIN api to view student's pending leave requests.
     * */

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(value = "/student-leave/view")
    public ResponseEntity<List<LeaveRequestDto>> viewStudentPendingLeaveRequests(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<List<LeaveRequestDto>>(leaveRequestService.viewStudentPendingLeaveRequests(username),
                HttpStatus.OK);
    }

    /**
     * TEACHER and ADMIN api to approve student's pending leave requests.
     * */

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PutMapping(value = "/student-leave/{leaveRequestId}/approve", produces = "application/json")
    public ResponseEntity<LeaveRequestDto> approveStudentLeaveRequest(@PathVariable Long leaveRequestId){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<LeaveRequestDto>(leaveRequestService.approveStudentLeaveRequest(username, leaveRequestId),
                HttpStatus.OK);
    }

    /**
     * TEACHER and ADMIN api to reject student's pending leave requests.
     * */

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PutMapping(value = "/student-leave/{leaveRequestId}/reject", produces = "application/json")
    public ResponseEntity<LeaveRequestDto> rejectStudentLeaveRequest(@PathVariable Long leaveRequestId,
                                                       String remarks){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<LeaveRequestDto>(leaveRequestService.rejectStudentLeaveRequest(username, leaveRequestId,
                remarks), HttpStatus.OK);
    }

}
