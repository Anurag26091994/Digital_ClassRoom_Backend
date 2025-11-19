package com.digital.serviceimpl;

import com.digital.dto.PTMRequest;
import com.digital.dto.PTMResponse;
import com.digital.entity.*;
import com.digital.enums.NotificationType;
import com.digital.enums.PTMStatus;
import com.digital.events.PTMScheduledEvent;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.GoogleMeetService;
import com.digital.servicei.PTMService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // <- make sure this is present
@Transactional
@RequiredArgsConstructor // <- generates constructor for final fields
public class PTMServiceImpl implements PTMService {

    private final PTMRepository ptmRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final GoogleMeetService googleMeetService;
    private final ApplicationEventPublisher eventPublisher;

    @org.springframework.transaction.annotation.Transactional
    @Override
    public PTMResponse schedulePTM(PTMRequest request) {

        // 1️⃣ Fetch logged-in teacher
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Teacher teacher = teacherRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found for username: " + username
                ));
        Long teacherId = teacher.getId();

        // 2️⃣ Fetch students by IDs
        List<Student> students = studentRepository.findAllById(request.getStudentIds());

        // 3️⃣ Create Google Meet link
        String meetLink = null;
        try {
            meetLink = googleMeetService.createGoogleMeetLink(
                    teacherId,
                    request.getTitle(),
                    request.getMeetingDateTime(),
                    request.getDurationMinutes()
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Optional: throw new RuntimeException("Failed to create Google Meet link");
        }

        // 4️⃣ Create PTM entity
        PTM ptm = PTM.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .meetingDateTime(request.getMeetingDateTime())
                .durationMinutes(request.getDurationMinutes())
                .type(request.getType())
                .venue(request.getVenue())
                .joinLink(meetLink)
                .status(PTMStatus.SCHEDULED)
                .students(students)
                .teacher(teacher)
                .build();

        ptm = ptmRepository.save(ptm);

        // 5️⃣ Publish PTM scheduled event for notifications
        List<Long> studentIds = students.stream()
                .map(Student::getStudentRegId)
                .collect(Collectors.toList());
        eventPublisher.publishEvent(new PTMScheduledEvent(ptm, studentIds));
        // 6️⃣ Build and return PTMResponse
        return PTMResponse.builder()
                .ptmId(ptm.getPtmId())
                .title(ptm.getTitle())
                .description(ptm.getDescription())
                .meetingDateTime(ptm.getMeetingDateTime())
                .durationMinutes(ptm.getDurationMinutes())
                .type(ptm.getType())
                .venue(ptm.getVenue())
                .joinLink(ptm.getJoinLink())
                .status(ptm.getStatus())
                .studentIds(studentIds)
                .build();
    }



    @Override
    public List<PTMResponse> getAllPTMs() {
        return ptmRepository.findAll().stream().map(ptm -> {
            List<Long> studentIds = ptm.getStudents()
                    .stream()
                    .map(Student::getStudentId)
                    .collect(Collectors.toList());

            return PTMResponse.builder()
                    .ptmId(ptm.getPtmId())
                    .title(ptm.getTitle())
                    .description(ptm.getDescription())
                    .meetingDateTime(ptm.getMeetingDateTime())
                    // .durationMinutes(ptm.getDurationMinutes())
                    .type(ptm.getType())
                    .venue(ptm.getVenue())
                    .status(ptm.getStatus())
                    .joinLink(ptm.getJoinLink())
                    .studentIds(studentIds)
                    .build();
        }).collect(Collectors.toList());
    }
}

