package com.digital.controller;

import com.digital.dto.EmailDto;
import com.digital.dto.ManagerStatusDto;
import com.digital.dto.ResetPasswordDto;
import com.digital.entity.User;
import com.digital.exception.BadRequestException;
import com.digital.servicei.UserServiceI;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceI userServiceI;

    public UserController(UserServiceI userServiceI) {
        this.userServiceI = userServiceI;
    }

    // ✅ OPTION 1: ADMIN creates user (dashboard)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<User> createUserByAdmin(
            @Valid @RequestBody User user) throws BadRequestException {

        log.info("ADMIN creating user: {}", user.getEmail());
        User response = userServiceI.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ OPTION 2: PUBLIC registration (no token)
    // 👉 Use this if students/teachers register themselves
    @PostMapping("/register")
    public ResponseEntity<User> publicRegister(
            @Valid @RequestBody User user) throws BadRequestException {

        log.info("PUBLIC registration: {}", user.getEmail());
        User response = userServiceI.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------- OTP ----------------
    @PostMapping("/otp")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody EmailDto emailDto) {

        String response = userServiceI.sendOtp(emailDto);
        return ResponseEntity.ok(response);
    }

    // ---------------- RESET PASSWORD ----------------
    @PutMapping("/password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordDto resetPasswordDto) {

        String response = userServiceI.resetPassword(resetPasswordDto);
        return ResponseEntity.ok(response);
    }

    // ---------------- APPROVE / REJECT USER ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{userId}")
    public ResponseEntity<?> manageUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody ManagerStatusDto manageStatusDto) {

        log.info("ADMIN managing user status: {}", userId);
        User response = userServiceI.manageUserStatus(userId, manageStatusDto);

        if (response == null) {
            return ResponseEntity.ok("User rejected successfully");
        }
        return ResponseEntity.ok(response);
    }

    // ---------------- GET ALL USERS ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userServiceI.getAllUsers());
    }

    // ---------------- GET USER BY ID ----------------
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'TEACHER', 'STUDENT', 'PARENT')")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userServiceI.getUserById(userId));
    }
}
