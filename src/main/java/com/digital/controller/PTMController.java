package com.digital.controller;

import com.digital.dto.PTMRequest;
import com.digital.dto.PTMResponse;
import com.digital.servicei.PTMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ptm")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PTMController {

    private final PTMService ptmService;

    /**
     * Schedule a new Parent-Teacher Meeting (PTM)
     */
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePTM(@RequestBody PTMRequest request) {
        try {
            PTMResponse response = ptmService.schedulePTM(request);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "PTM scheduled successfully",
                    "data", response
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Retrieve all scheduled PTMs (accessible by Teacher/Admin)
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<?> getAllPTMs() {
        try {
            List<PTMResponse> ptms = ptmService.getAllPTMs();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", ptms.size(),
                    "data", ptms
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }
}
