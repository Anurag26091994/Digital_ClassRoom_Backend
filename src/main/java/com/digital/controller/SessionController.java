package com.digital.controller;

import com.digital.entity.Session;
import com.digital.servicei.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * This api allows TEACHER to create sessions for students
     * */

//    @PreAuthorize("hasRole('TEACHER')")
//    @PostMapping(consumes = "application/json", produces = "text/plain")
//    public ResponseEntity<String> createSession(@Valid @RequestBody SessionRequest sessionRequest){
//        return new ResponseEntity<>(sessionService.createSession(sessionRequest), HttpStatus.CREATED);
//    }

    /**
     * This api allows TEACHER, STUDENT, ADMIN to fetch session
     * */

    @PreAuthorize("hasAnyRole('Teacher', 'STUDENT', 'ADMIN')")
    @GetMapping(path = "/get", produces = "application/json")
    public ResponseEntity<List<Session>> getAllSessions(){
        return new ResponseEntity<>(sessionService.getAllSessions(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping(path = "/joinLink/{sessionId}")
    public ResponseEntity<String> addJoinLink(@PathVariable Long sessionId) throws IOException {
        return new ResponseEntity<>(sessionService.addJoinLink(sessionId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping(path = "/teacher/{id}", produces = "application/json")
    public ResponseEntity<List<Session>> getTeacherSessions(@PathVariable Long id){
        return new ResponseEntity<>(sessionService.getTeacherSessions(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping(path = "/student/{sectionId}", produces = "application/json")
    public ResponseEntity<List<Session>> getStudentSessions(@PathVariable Long sectionId){
        return new ResponseEntity<>(sessionService.getStudentSessions(sectionId), HttpStatus.OK);
    }
}
