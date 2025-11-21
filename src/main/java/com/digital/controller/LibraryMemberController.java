package com.digital.controller;

import com.digital.dto.*;
import com.digital.servicei.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LibraryMemberController {

    private final BookService bookService;
    private final IssueService issueService;
    private final FineService fineService;
    private final ReservationService reservationService;
    private final MemberService memberService;

    @PostMapping("/Member")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    public ResponseEntity<?> createMember(@RequestBody MemberCreateRequest req) {
        try {
            MemberDTO created = memberService.createMember(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Cannot create member: " + ex.getMessage()));
        }
    }

    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    public ResponseEntity<?> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category) {
        try {
            List<BookDTO> list = bookService.searchBooks(title, author, category);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error searching books: " + ex.getMessage()));
        }
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> reserveBook(
            @RequestParam Long bookId,
            @RequestParam Long memberId) {
        try {
            ReservationDTO dto = reservationService.reserveBook(bookId, memberId);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Cannot reserve book: " + ex.getMessage()));
        }
    }

    @GetMapping("/my-issues")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    public ResponseEntity<?> myIssues(@RequestParam Long memberId) {
        try {
            List<IssueDTO> list = issueService.listIssuesByMember(memberId);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching issues: " + ex.getMessage()));
        }
    }

    @PostMapping("/renew/{issueId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> renew(@PathVariable Long issueId) {
        try {
            IssueDTO dto = issueService.renewBook(issueId);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Cannot renew: " + ex.getMessage()));
        }
    }

    @PostMapping("/fines")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<FineDTO> createFine(@RequestBody FineRequest request) {
        FineDTO fineDTO = fineService.createFine(
                request.getIssueId(),
                request.getReason(),
                request.getOverrideAmount()
        );
        return new ResponseEntity<>(fineDTO, HttpStatus.CREATED);
    }

    @GetMapping("/fines")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> getFines(@RequestParam Long memberId) {
        try {
            List<FineDTO> list = fineService.listFinesForMember(memberId);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error getting fines: " + ex.getMessage()));
        }
    }

    @PostMapping("/fines/pay")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> payFine(
            @RequestParam Long fineId,
            @RequestParam Long paymentId) {
        try {
            FineDTO dto = fineService.payFine(fineId, paymentId);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Cannot pay fine: " + ex.getMessage()));
        }
    }

    // Standard API response wrapper
    public static class ApiResponse {
        public boolean success;
        public String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
