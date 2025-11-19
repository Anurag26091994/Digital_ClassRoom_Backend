package com.digital.controller;

import com.digital.dto.TimetableRequest;
import com.digital.dto.UpdateTimetableRequest;
import com.digital.entity.Timetable;
import com.digital.servicei.TimetableService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/admin/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> createTimetable(@Valid @RequestBody TimetableRequest timetableRequest){
        return new ResponseEntity<>(timetableService.createTimetable(timetableRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{timetableId}", consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> updateTimetable(@PathVariable Long timetableId, @RequestBody UpdateTimetableRequest request){
        return new ResponseEntity<>(timetableService.updateTimetable(timetableId, request), HttpStatus.OK);
    }


    // Here we are returning List<Timetable> but because of frontend we have changed the return type to List<TimetableResponse>

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Timetable>> getTimetables(){
        return new ResponseEntity<List<Timetable>>(timetableService.getTimetables(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{timetableId}")
    public ResponseEntity<String> deleteTimetable(@PathVariable Long timetableId){
        return new ResponseEntity<>(timetableService.deleteTimetable(timetableId), HttpStatus.OK);
    }
}
