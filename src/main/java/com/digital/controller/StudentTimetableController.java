package com.digital.controller;

import com.digital.entity.Session;
import com.digital.entity.Timetable;
import com.digital.servicei.SessionService;
import com.digital.servicei.TimetableService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/student")
@AllArgsConstructor
public class StudentTimetableController {

    private final TimetableService timetableService;
    private final SessionService sessionService;

    @GetMapping(value = "/timetable/{sectionId}", produces = "application/json")
    public ResponseEntity<List<Timetable>> getStudentTimetableBySectionId(@PathVariable Long sectionId){
        return new ResponseEntity<>(timetableService.getStudentTimetableBySectionId(sectionId), HttpStatus.OK);
    }

    @GetMapping(value = "/sessions/{sectionId}", produces = "application/json")
    public ResponseEntity<List<Session>> getStudentSessionsBySectionId(@PathVariable Long sectionId){
        return new ResponseEntity<>(sessionService.getStudentSessions(sectionId), HttpStatus.OK);
    }
}
