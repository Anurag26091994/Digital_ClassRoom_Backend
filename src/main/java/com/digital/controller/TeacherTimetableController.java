package com.digital.controller;

import com.digital.entity.Session;
import com.digital.entity.Timetable;
import com.digital.servicei.SessionService;
import com.digital.servicei.TeacherTimetableService;
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
@PreAuthorize("hasRole('TEACHER')")
public class TeacherTimetableController {

    private final TeacherTimetableService teacherTimetableService;
    private final SessionService sessionService;

    @GetMapping(path = "/timetable/{id}", produces = "application/json")
    public ResponseEntity<List<Timetable>> getTimetableByTeacherId(@PathVariable Long id){
        return new ResponseEntity<>(teacherTimetableService.getTimetableByTeacherId(id), HttpStatus.OK);
    }

    @GetMapping(path = "/sessions/{id}", produces = "application/json")
    public ResponseEntity<List<Session>> getSessionsByTeacherId(@PathVariable Long id){
        return new ResponseEntity<>(sessionService.getTeacherSessions(id), HttpStatus.OK);
    }
}
