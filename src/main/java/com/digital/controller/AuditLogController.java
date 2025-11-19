package com.digital.controller;

import com.digital.entity.AuditLog;
import com.digital.servicei.AuditLogServiceI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/auditLog")
public class AuditLogController {

    private final AuditLogServiceI auditLogServiceI;

    public AuditLogController(AuditLogServiceI auditLogServiceI) {
        this.auditLogServiceI = auditLogServiceI;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUserId(@PathVariable Long userId){
        return new ResponseEntity<List<AuditLog>>(auditLogServiceI.getAuditLogsByUserId(userId), HttpStatus.OK);
    }

    // This api is for every user

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'LIBRARIAN', 'TRANSPORT')")
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogsByUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Username: " + authentication.getName());
        return new ResponseEntity<List<AuditLog>>(auditLogServiceI.getAuditLogsByUsername(authentication.getName()), HttpStatus.OK);
    }
}
