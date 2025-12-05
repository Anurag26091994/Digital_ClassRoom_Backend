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


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<User> createNewUser(@Valid @RequestBody User user) throws BadRequestException {
        log.info("API Hit: Register User");
        User response = userServiceI.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/otp")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailDto emailDto) {
        String response = userServiceI.sendOtp(emailDto);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        String response = userServiceI.resetPassword(resetPasswordDto);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{userId}")
    public ResponseEntity<?> manageUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody ManagerStatusDto manageStatusDto
    ) {
        log.info("API Hit: Approve User ID {}", userId);
        User response = userServiceI.manageUserStatus(userId, manageStatusDto);
        if (response == null) {
            return ResponseEntity.ok("Student rejected successfully");
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userServiceI.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userServiceI.getUserById(userId);
        return ResponseEntity.ok(user);
    }


}
