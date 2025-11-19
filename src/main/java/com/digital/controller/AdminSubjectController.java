package com.digital.controller;
import com.digital.dto.SubjectRequest;
import com.digital.dto.SubjectResponse;
import com.digital.servicei.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin/subjects")
@RequiredArgsConstructor
public class AdminSubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@RequestBody SubjectRequest request) {
        return ResponseEntity.ok(subjectService.createSubject(request));
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> update(@PathVariable Long subjectId,
                                                  @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(subjectService.updateSubject(subjectId, request));
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> delete(@PathVariable Long subjectId) {
        subjectService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAll() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> getById(@PathVariable Long subjectId) {
        return ResponseEntity.ok(subjectService.getSubjectById(subjectId));
    }
}
