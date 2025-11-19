package com.digital.serviceimpl;

import com.digital.entity.Admin;
import com.digital.enums.Role;
import com.digital.enums.Status;
import com.digital.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminInitializer implements CommandLineRunner {

    @Value(value = "${admin.username}")
    private String username;

    @Value(value = "${admin.password}")
    private String password;

    private final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    public AdminInitializer(PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(adminRepository.findByUsername(username).isPresent())
            logger.info("Admin record is already registered in database");

        else {
            Admin admin = Admin.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();

            adminRepository.save(admin);
        }
    }
}
