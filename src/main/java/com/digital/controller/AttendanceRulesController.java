package com.digital.controller;

import com.digital.entity.AttendanceRule;
import com.digital.enums.AttendanceRuleName;
import com.digital.servicei.AttendanceRulesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/attendanceRules")
@PreAuthorize("hasRole('ADMIN')")
public class AttendanceRulesController {

    private final AttendanceRulesService attendanceRulesService;

    public AttendanceRulesController(AttendanceRulesService attendanceRulesService) {
        this.attendanceRulesService = attendanceRulesService;
    }

    /**
     * This api allows ADMIN to set attendance rule for PRESENT, ABSENT, LATE and HALF-DAY
     * Based on these attendance rules, student's attendance status is decided.
     * */

    @PostMapping(consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> setRule(@Valid @RequestBody AttendanceRule attendanceRules){
        return new ResponseEntity<>(attendanceRulesService.setRule(attendanceRules), HttpStatus.CREATED);
    }

    /**
     * This api allows ADMIN to fetch attendance rule by attendance rule name.
     * */

    @GetMapping(value = "/ruleName", produces = "application/json")
    public ResponseEntity<AttendanceRule> getAttendanceRule(@RequestParam AttendanceRuleName ruleName){
        return new ResponseEntity<>(attendanceRulesService.getAttendanceRule(ruleName), HttpStatus.OK);
    }

    /**
     * This api allows ADMIN to fetch all attendance rules.
     * */

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<AttendanceRule>> getAllAttendanceRule(){
        return new ResponseEntity<>(attendanceRulesService.getAllAttendanceRule(), HttpStatus.OK);
    }

    /**
     * This api allows ADMIN to update attendance rule values.
     * */

    @PutMapping(value = "/{attendanceRuleId}", consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> updateRule(@PathVariable Long attendanceRuleId, @RequestBody AttendanceRule attendanceRule){
        return new ResponseEntity<>(attendanceRulesService.updateRule(attendanceRuleId, attendanceRule), HttpStatus.OK);
    }

    /**
     * This api allows ADMIN to delete attendance rule by attendance rule id.
     * */

    @DeleteMapping(value = "/{attendanceRuleId}")
    public ResponseEntity<String> deleteRule(@PathVariable Long attendanceRuleId){
        return new ResponseEntity<>(attendanceRulesService.deleteRule(attendanceRuleId), HttpStatus.OK);
    }
}
