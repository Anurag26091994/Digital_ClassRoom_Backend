package com.digital.google_meet_config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class GoogleAuthController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.auth.url}")
    private String authUrl;

    // Once we connect backend to frontend after that we will not need teacherId we will use SecurityContextHolder to
    // get username then we will change the code
    //@PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/google/{id}")
    public void googleLogin(HttpServletResponse response, @PathVariable Long id) throws IOException {

        // String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String url = authUrl
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=https://www.googleapis.com/auth/calendar.events"
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + id;
        response.sendRedirect(url);
    }

//    @PreAuthorize("hasRole('TEACHER')")
//    @GetMapping("/google")
//    public void googleLogin(HttpServletResponse response) throws IOException {
//
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        String url = authUrl
//                + "?client_id=" + clientId
//                + "&redirect_uri=" + redirectUri
//                + "&response_type=code"
//                + "&scope=https://www.googleapis.com/auth/calendar.events"
//                + "&access_type=offline"
//                + "&prompt=consent"
//                + "&state=" + username;
//        response.sendRedirect(url);
//    }
}
