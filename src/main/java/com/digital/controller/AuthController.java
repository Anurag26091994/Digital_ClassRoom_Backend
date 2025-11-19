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
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserServiceI userServiceI;

    private final AuditLogServiceI auditLogServiceI;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserServiceI userServiceI, AuditLogServiceI auditLogServiceI) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userServiceI = userServiceI;
        this.auditLogServiceI = auditLogServiceI;
    }

    @PostMapping(path = "/login", consumes="application/json")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody RequestDto requestDto){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        Map<String, String> response = new HashMap<>();

        if(authentication.isAuthenticated()){

            String username = authentication.getName();

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

            // Always include role in response (remove "ROLE_" prefix)
            String roleValue = role.replace("ROLE_", "");

            if(!role.equals("ROLE_ADMIN")){
                User user = userServiceI.findUserByUsername(username);

                auditLogServiceI.logInfo(user.getUserId(), user.getUsername(), Action.LOGIN, Module.USER_MODULE);

                user.setLastLogin(LocalDateTime.now());
                userServiceI.updateUser(user);
            }

            response.put("role", roleValue);

            String token = jwtService.generateToken(username, role);

            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if(!role.equals("ROLE_ADMIN")) {
            User user = userServiceI.findUserByUsername(authentication.getName());
            auditLogServiceI.logInfo(user.getUserId(), user.getUsername(), Action.LOGOUT, Module.USER_MODULE);
        }

        SecurityContextHolder.clearContext();

        return new ResponseEntity<String>(role.substring(5) + " logout successfully", HttpStatus.OK);
    }
}