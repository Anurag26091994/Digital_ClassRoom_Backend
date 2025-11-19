package com.digital.securityConfig;

import com.digital.exception.CustomUnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AppFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AppFilter.class);

    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AppFilter(CustomUserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            String token = null;
            String username = null;

            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                username = jwtService.extractUsername(token);
                System.out.println("username " + username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("✅ User '{}' authenticated with roles: {}",
                        userDetails.getUsername(),
                        userDetails.getAuthorities());
                } else {
                    logger.warn("❌ Invalid JWT token for user {}", username);
                    throw new CustomUnauthorizedException("Invalid or expired JWT token");
                }
            }

            filterChain.doFilter(request, response);

        } catch (CustomUnauthorizedException e) {
            // Explicit 401 (handled by your @ControllerAdvice or entrypoint)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Unexpected error in JWT authentication", e);
            // Fail-safe: 500 instead of wrongly sending 403
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication filter error: " + e.getMessage() + "\"}");
        }
    }
}
