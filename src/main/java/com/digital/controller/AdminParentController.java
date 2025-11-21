package com.digital.controller;

import com.digital.dto.ParentRequest;
import com.digital.dto.ParentResponse;
import com.digital.servicei.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/parents")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminParentController {
    private final ParentService parentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createParent(@Valid @RequestBody ParentRequest req) {
        try {
            ParentResponse resp = parentService.createParent(req);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException ex) {
            // Return custom JSON error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParentResponse>updateParent(@PathVariable("id") Long id, @Valid @RequestBody ParentRequest req){
        ParentResponse resp = parentService.updateParent(id, req);
        return ResponseEntity.ok(resp);
    }
    @DeleteMapping("/parents/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.ok("Parent deleted successfully");
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParentResponse>>getAll(){
        return ResponseEntity.ok(parentService.getAllParents());
    }

    @PostMapping("/link")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> linkParentToStudent(
            @RequestParam Long parentId,
            @RequestParam Long studentId,
            @RequestParam String relationship) {

        parentService.linkParentToStudent(parentId, studentId, relationship);
        return ResponseEntity.ok("Parent linked to student successfully");
    }

}
