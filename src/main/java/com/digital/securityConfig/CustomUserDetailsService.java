package com.digital.securityConfig;

import com.digital.entity.Admin;
import com.digital.entity.User;
import com.digital.enums.Status;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.AdminRepository;
import com.digital.repository.UserRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Special case: hardcoded admin
        if (username.equals("apdhanorkar94@gmail.com")) {
            Admin admin = adminRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin record not found for username: " + username));

            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    getAuthorities(admin)
            );
        }

        // Otherwise, load a regular user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User record not found for username: " + username));

        if (user.getStatus() == Status.INACTIVE) {
            throw new AuthorizationDeniedException("Inactive users cannot login");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user)
        );
    }

    private Collection<SimpleGrantedAuthority> getAuthorities(User user) {
        String role = String.valueOf(user.getRole());
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private Collection<SimpleGrantedAuthority> getAuthorities(Admin admin) {
        String role = String.valueOf(admin.getRole());
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
