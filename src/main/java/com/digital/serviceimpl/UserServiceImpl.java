package com.digital.serviceimpl;

import com.digital.dto.EmailDto;
import com.digital.dto.ManagerStatusDto;
import com.digital.dto.ResetPasswordDto;
import com.digital.entity.User;
import com.digital.enums.Action;
import com.digital.enums.Module;
import com.digital.enums.Status;
import com.digital.exception.BadRequestException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.UserRepository;
import com.digital.servicei.AuditLogServiceI;
import com.digital.servicei.UserServiceI;
import com.digital.util.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class UserServiceImpl implements UserServiceI {

    @Value("${spring.mail.username}")
    private String from;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final AuditLogServiceI auditLogServiceI;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JavaMailSender javaMailSender,
                           AuditLogServiceI auditLogServiceI,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.auditLogServiceI = auditLogServiceI;
        this.emailService = emailService;
    }

    // ---------------- LOGIN SUPPORT (EMAIL BASED) ----------------
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with email '" + email + "' not found"));
    }

    // ---------------- OLD METHOD (KEEP FOR OTHER FEATURES) ----------------
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username '" + username + "' not found"));
    }

    // ---------------- REST CODE (UNCHANGED) ----------------

    @Override
    public User add(User user) throws BadRequestException {

        // 🔥 FIX 1: username null
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            user.setUsername(user.getEmail());
        }

        // 🔥 FIX 2: username check
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        // 🔥 FIX 3: email check
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // 🔥 FIX 4: password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 🔥 FIX 5: defaults
        user.setApproved(false);
        user.setStatus(Status.INACTIVE);

        User savedUser = userRepository.save(user);


        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(savedUser.getEmail());
            message.setSubject("Registration Successful");
            message.setText("Your registration is submitted. Please wait for admin approval.");
            javaMailSender.send(message);
        } catch (Exception e) {
            log.warn("Mail failed: {}", e.getMessage());
        }

        return savedUser;
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String sendOtp(EmailDto emailDto) {
        User user = findUserByEmail(emailDto.getEmail());

        String otp = String.format("%06d", new Random().nextInt(1000000));
        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpGenerationTime(LocalDateTime.now());
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("OTP");
        message.setText("Your OTP is: " + otp);
        javaMailSender.send(message);

        return "OTP sent successfully";
    }

    @Override
    public String resetPassword(ResetPasswordDto dto) {

        User user = findUserByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getOtp(), user.getOtp())) {
            return "Invalid OTP";
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setOtp(null);
        user.setOtpGenerationTime(null);
        userRepository.save(user);

        auditLogServiceI.logInfo(
                user.getUserId(),
                user.getUsername(),
                Action.PASSWORD_CHANGE,
                Module.USER_MODULE
        );

        return "Password reset successfully";
    }

    @Override
    public User manageUserStatus(Long userId, ManagerStatusDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (dto.isApproved()) {
            user.setStatus(Status.ACTIVE);
            user.setApproved(true);
            userRepository.save(user);

            emailService.sendMail(
                    user.getEmail(),
                    "Account Approved",
                    "Your account has been approved."
            );
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }
}
