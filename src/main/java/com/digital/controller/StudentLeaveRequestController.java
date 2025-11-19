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
@RequestMapping(value = "/student/leaves")
@PreAuthorize("hasRole('STUDENT')")
public class StudentLeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * STUDENT api to apply for leave.
     * */

    @PostMapping(value = "/apply", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LeaveRequestDto> applyForLeave(@RequestBody MakeLeaveRequest makeLeaveRequest){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<LeaveRequestDto>(leaveRequestService.applyForLeave(makeLeaveRequest, username),
                HttpStatus.CREATED);
    }

    /**
     * STUDENT api to view leave request status.
     * */

    @GetMapping(value = "/status", produces = "application/json")
    public ResponseEntity<List<LeaveRequestDto>> viewLeaveApprovalStatus(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<List<LeaveRequestDto>>(leaveRequestService.viewLeaveApprovalStatus(username),
                HttpStatus.OK);
    }
}
