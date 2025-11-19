package com.digital.controller;

import com.digital.dto.IssueDTO;
import com.digital.dto.ReservationDTO;
import com.digital.exception.BusinessException;
import com.digital.exception.NotFoundException;
import com.digital.servicei.IssueService;
import com.digital.servicei.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adminLibrarian")
@RequiredArgsConstructor
public class LibrarianController {

    private final IssueService issueService;
    private final ReservationService reservationService;

    // DTO for issue request body
    public static class IssueRequest {
        public Long bookId;
        public Long memberId;
    }

    // Utility method to build consistent response body
    private ResponseEntity<?> buildResponse(boolean success, String message, Object data, HttpStatus status) {
        return ResponseEntity.status(status).body(
                new Response(success, message, data)
        );
    }

    // Wrapper response body class
    public static class Response {
        public boolean success;
        public String message;
        public Object data;

        public Response(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }


    @PostMapping("/issue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> issueBook(@RequestBody IssueRequest req) {
        try {
            IssueDTO dto = issueService.issueBook(req.bookId, req.memberId);
            return buildResponse(true, "Book issued successfully", dto, HttpStatus.OK);
        } catch (Exception ex) {
            return buildResponse(false, "Could not issue book: " + ex.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/return/{issueId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> returnBook(@PathVariable Long issueId) {
        try {
            IssueDTO dto = issueService.returnBook(issueId);
            return buildResponse(true, "Book returned successfully", dto, HttpStatus.OK);
        } catch (Exception ex) {
            return buildResponse(false, "Return failed: " + ex.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/renew/{issueId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> renewBook(@PathVariable Long issueId) {
        try {
            IssueDTO dto = issueService.renewBook(issueId);
            return buildResponse(true, "Book renewed successfully", dto, HttpStatus.OK);
        } catch (Exception ex) {
            return buildResponse(false, "Renewal failed: " + ex.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/issues/member/{memberId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> issuesByMember(@PathVariable Long memberId) {
        try {
            List<IssueDTO> list = issueService.listIssuesByMember(memberId);
            return buildResponse(true, "Issues fetched successfully", list, HttpStatus.OK);
        } catch (Exception ex) {
            return buildResponse(false, "Error fetching issues: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/issues/overdue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> overdueIssues() {
        try {
            List<IssueDTO> list = issueService.listOverdueIssues();
            return buildResponse(true, "Overdue issues fetched", list, HttpStatus.OK);
        } catch (Exception ex) {
            return buildResponse(false, "Error fetching overdue issues: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reservation/approve/{resId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> approveReservation(@PathVariable Long resId) {
        try {
            ReservationDTO dto = reservationService.approveReservation(resId);
            return buildResponse(true, "Reservation approved and book issued", dto, HttpStatus.OK);
        } catch (BusinessException ex) {
            return buildResponse(false, "Approval failed: " + ex.getMessage(), null, HttpStatus.CONFLICT);
        } catch (NotFoundException ex) {
            return buildResponse(false, "Approval failed: " + ex.getMessage(), null, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return buildResponse(false, "Unexpected error: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reservation/cancel/{resId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> cancelReservation(@PathVariable Long resId) {
        try {
            ReservationDTO dto = reservationService.cancelReservation(resId);
            return buildResponse(true, "Reservation cancelled successfully", dto, HttpStatus.OK);
        } catch (BusinessException ex) {
            return buildResponse(false, "Cancellation failed: " + ex.getMessage(), null, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException ex) {
            return buildResponse(false, "Cancellation failed: " + ex.getMessage(), null, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return buildResponse(false, "Unexpected error: " + ex.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    private ResponseEntity<?> buildResponse(boolean success, String message, Object data, HttpStatus status) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("success", success);
//        body.put("message", message);
//        body.put("data", data);
//        return new ResponseEntity<>(body, status);
//    }
}