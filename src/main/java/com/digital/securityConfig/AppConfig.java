package com.digital.securityConfig;

import com.digital.exception.CustomForbiddenException;
import com.digital.exception.CustomUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    private final CustomUserDetailsService userDetailsService;
    private final AppFilter appFilter;

    public AppConfig(CustomUserDetailsService userDetailsService, AppFilter appFilter) {
        this.userDetailsService = userDetailsService;
        this.appFilter = appFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ===== CORS Filter =====
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200"); // Angular frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Building Security Filter Chain...");

        http.csrf(AbstractHttpConfigurer::disable)
                .cors().and()
                .authorizeHttpRequests(auth -> auth

                        // auth/login
                        .requestMatchers("/api/auth/**").permitAll()

                        // user APIs
                        .requestMatchers("/api/user/otp", "/api/user/password").permitAll()
                        .requestMatchers("/api/user/create", "/api/user/status/*").hasRole("ADMIN")

                        // audit log
                        .requestMatchers("/api/auditLog").hasAnyRole("TEACHER", "STUDENT", "LIBRARIAN", "TRANSPORT")
                        .requestMatchers("/api/auditLog/*").hasRole("ADMIN")

                        // teacher APIs
                        .requestMatchers("/api/teacher/fetch-all").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers("/api/teacher/**").hasRole("ADMIN")

                        // attendance rule apis
                        .requestMatchers( "/api/attendanceRules/**").hasRole("ADMIN")

                        // student attendance
                        .requestMatchers("/api/attendance/join-session/*",
                                                  "/api/attendance/leave-session/*").hasRole("STUDENT")

                        // teacher attendance
                        .requestMatchers("/api/attendance/auto-mark/*",
                                "/api/attendance/check-in-list/*",
                                "/api/attendance/view/*",
                                "/api/attendance/view-all/*",
                                "/api/attendance/update/*").hasRole("TEACHER")

                        // admin attendance
                        .requestMatchers("/api/attendance/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/attendance/admin/pdf/*").hasAnyRole("ADMIN", "TEACHER")

                        //report
                        .requestMatchers("/api/admin/exams/report-cards/generate")
                        .hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/reports/**").permitAll()   // static reports folder
                        .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "PARENT")  // secure API
                        .requestMatchers("/api/teacher/**").hasAnyRole("ADMIN", "TEACHER")

                        // subject apis
                        .requestMatchers("/api/admin/subject", "/api/admin/subject/*",
                         "/api/admin/subject/class/*", "/api/admin/subject/teacher/*",
                         "/api/admin/subject/update/*").hasRole("ADMIN")

                        // admin timetable apis
                        .requestMatchers("/api/admin/timetable/**").hasRole("ADMIN")

                        // teacher timetable and session apis - TeacherTimetableController
                        .requestMatchers("/teacher/timetable/*",
                                                  "/teacher/sessions/*").hasRole("TEACHER")

                        // student timetable and session apis - StudentTimetableController
                        .requestMatchers("/student/timetable/*",
                                                  "/student/sessions/*").hasRole("STUDENT")

                        // class session apis - SessionController
                        .requestMatchers("/api/session/joinLink").hasRole("TEACHER")
                        .requestMatchers("/api/session/get").hasAnyRole("TEACHER", "STUDENT", "ADMIN")

                        // Google Meet apis
                        .requestMatchers("/auth/google/*").permitAll()
                        .requestMatchers("/oauth2/callback/**").permitAll()

                        // teacher assignments - TeacherAssignmentController
                        .requestMatchers("/teacher/assignments/**").hasRole("TEACHER")

                        // class/section
                        .requestMatchers("/api/class/fetch-all",
                                                  "/api/section/classes/*/sections/fetch-all")
                        .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/api/class/**",
                                                  "/api/section/**").hasRole("ADMIN")

                        // students
                        .requestMatchers("/api/students/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/students/**").hasRole("STUDENT")

                        // exams, fee and payment apis

                        .requestMatchers("/api/admin/**",
                                                  "/api/gateway**").hasRole("ADMIN")
                        .requestMatchers("/api/payment/**").hasRole("STUDENT")
                        .requestMatchers("/api/studentFee/invoices/all").hasRole("ADMIN")
                        .requestMatchers("/api/studentFee/**").hasRole("STUDENT")

                        // ADMIN Academic Calendar Apis
                        .requestMatchers("/admin/calendar",
                                                  "/admin/calendar/*/holidays",
                                                  "/admin/calendar/*/events",
                                                  "/admin/calendar/view-all",
                                                  "/admin/calendar/view/*",
                                                  "/admin/calendar/*",
                                                  "/admin/calendar/holidays/*/view",
                                                  "/admin/calendar/holidays/view-all",
                                                  "/admin/calendar/holidays/*",
                                                  "/admin/calendar/holidays/remove/*",
                                                  "/admin/calendar/events/*",
                                                  "/admin/calendar/events/*/view",
                                                  "/admin/calendar/events/view-all",
                                                  "/admin/calendar/events/remove/*").hasRole("ADMIN")

                        // TEACHER view calendar events Api
                        .requestMatchers("/teacher/calendar").hasRole("TEACHER")

                        // STUDENT view calender events Api
                        .requestMatchers("/student/calendar").hasRole("STUDENT")

                        // Holiday Event Notifications
                        .requestMatchers("/holiday-event-notification",
                                                  "/holiday-event-notification/seen/*").permitAll()

                        // Student Leave request apis
                        .requestMatchers("/student/leaves/apply",
                                                  "/student/leaves/status").hasRole("STUDENT")

                        // Teacher Leave request apis
                        .requestMatchers("/teacher/leaves/apply").hasRole("TEACHER")
                        .requestMatchers("/teacher/student-leave/view",
                                                  "/teacher/student-leave/*/approve",
                                                  "/teacher/student-leave/*/reject").hasAnyRole("TEACHER", "ADMIN")

                        //LibraryModule apis
                        .requestMatchers("/admin/books/**").hasRole("LIBRARIAN")
                        .requestMatchers("/library/**").hasAnyRole("STUDENT", "TEACHER","LIBRARIAN")
                        .requestMatchers("/library").hasRole("LIBRARIAN") // POST /library to createFine

                        .requestMatchers("/adminLibrarian/**").hasRole("LIBRARIAN")

                        // Admin Leave request apis
                        .requestMatchers("/admin/leave-requests",
                                                  "/admin/leave-requests/*/approve",
                                                  "/admin/leave-requests/*/reject").hasRole("ADMIN")

                        // any other API requires auth
                        .requestMatchers("/api/**").authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(appFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            throw new CustomUnauthorizedException("Unauthorized - Please log in");
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            throw new CustomForbiddenException("Forbidden - You do not have access to this resource");
        };
    }
}
