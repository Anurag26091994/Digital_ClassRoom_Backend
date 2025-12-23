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

    @Value(value = "${spring.mail.username}")
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
        this.emailService=emailService;
    }

    @Override
    public User add(User user) throws BadRequestException {
        log.info("User registration attempt: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        String normalPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully", savedUser.getUsername());

        // Send email with credentials
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(savedUser.getEmail());
        message.setTo(from);
        message.setSubject("Digital Classroom Credentials");
        message.setText("Please Approve following Digital Classroom Form:\n\n"
                + "Username: " + savedUser.getUsername() + "\n"
                + "Role: " + savedUser.getRole() + "\n\n"
                + "Link for Approve the Form: ");
        javaMailSender.send(message);

//        emailService.sendMail(
//                from,
//                "Digital Classroom Credentials",
//                "Your Digital Classroom credentials are:" +
//                        "Username: "+ savedUser.getUsername()+
//                        "Password: " + normalPassword +
//                        "Please reset the password before login."
//        );

        return savedUser;
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username '" + username + "' not found"));
    }

    @Override
    public String sendOtp(EmailDto emailDto) {
        User user = userRepository.findByEmail(emailDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email address"));

        String otp = String.format("%06d", new Random().nextInt(1000000));

        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpGenerationTime(LocalDateTime.now());
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Digital Classroom System OTP");
        message.setText("Your OTP is: " + otp + "\nThis OTP is valid for 2 minutes.");
        javaMailSender.send(message);

        return "OTP has been sent to the given email.";
    }

    @Override
    public String resetPassword(ResetPasswordDto resetPasswordDto) {
        User user = userRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email"));

        LocalDateTime otpExpiryTime = user.getOtpGenerationTime().plusMinutes(2);
        if (LocalDateTime.now().isAfter(otpExpiryTime)) {
            return "OTP is expired";
        }

        if (passwordEncoder.matches(resetPasswordDto.getOtp(), user.getOtp())) {
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            user.setOtp(null);
            user.setOtpGenerationTime(null);

            userRepository.save(user);

            auditLogServiceI.logInfo(
                    user.getUserId(),
                    user.getUsername(),
                    Action.PASSWORD_CHANGE,
                    Module.USER_MODULE
            );

            return "Password reset successfully.";
        } else {
            return "Invalid OTP.";
        }
    }

    @Override
    public User manageUserStatus(Long userId, ManagerStatusDto manageStatusDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        if(manageStatusDto.isApproved()) {
            user.setStatus(Status.ACTIVE);
            user.setApproved(true);

            User updated = userRepository.save(user);
            log.info("Admin approved the student {}", updated.getUsername());

            // Mail to user
            emailService.sendMail(
                    updated.getEmail(),
                    "Account Approved",
                    "Your account has been approved. You can now log in."
            );

            log.info("User approved: {}", updated.getUsername());

            return updated;
        }
        else {
//            userRepo.delete(user);

            log.warn("Admin rejected the student {}", user.getUsername());

            emailService.sendMail(
                    user.getEmail(),
                    "Registration Rejected",
                    "Sorry! Your registration request has been rejected."
            );
//
           return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found in the database.");
        }
        return users;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    }
}
