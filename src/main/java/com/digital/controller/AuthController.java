package com.digital.controller;

import com.digital.dto.RequestDto;
import com.digital.entity.User;
import com.digital.enums.Action;
import com.digital.enums.Module;
import com.digital.exception.ResourceNotFoundException;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.AuditLogServiceI;
import com.digital.servicei.UserServiceI;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserServiceI userServiceI;
    private final AuditLogServiceI auditLogServiceI;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserServiceI userServiceI,
                          AuditLogServiceI auditLogServiceI) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userServiceI = userServiceI;
        this.auditLogServiceI = auditLogServiceI;
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody RequestDto requestDto) {

        Map<String, String> response = new HashMap<>();

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    requestDto.getUsername(),
                                    requestDto.getPassword()
                            )
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String email = authentication.getName();

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Role not found"));

            String token = jwtService.generateToken(email, role);

            // USER AUDIT (NON-ADMIN)
            if (!role.equals("ROLE_ADMIN")) {

                User user = userServiceI.findUserByEmail(email);

                user.setLastLogin(LocalDateTime.now());
                userServiceI.updateUser(user);

                auditLogServiceI.logInfo(
                        user.getUserId(),
                        user.getUsername(),
                        Action.LOGIN,
                        Module.USER_MODULE
                );
            }

            response.put("token", token);
            response.put("role", role.replace("ROLE_", ""));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Invalid email or password");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    // ---------------- LOGOUT ----------------
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_UNKNOWN");

            if (!role.equals("ROLE_ADMIN")) {
                User user = userServiceI.findUserByEmail(authentication.getName());

                auditLogServiceI.logInfo(
                        user.getUserId(),
                        user.getUsername(),
                        Action.LOGOUT,
                        Module.USER_MODULE
                );
            }
        }

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }
}