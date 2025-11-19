package com.digital.serviceimpl;

import com.digital.entity.Session;
import com.digital.exception.ResourceNotFoundException;
import com.digital.google_meet_config.GoogleTokenService;
import com.digital.repository.*;
import com.digital.servicei.GoogleRefreshTokenService;
import com.digital.servicei.SessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.auth.url}")
    private String authUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private final SessionRepository sessionRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;
    private final GoogleRefreshTokenService googleRefreshTokenService;
    private final GoogleTokenService googleTokenService;

    public SessionServiceImpl(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, SessionRepository sessionRepository, ClassRepository classRepository, SectionRepository sectionRepository, TeacherRepository teacherRepository, GoogleRefreshTokenService googleRefreshTokenService, GoogleTokenService googleTokenService) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com/calendar/v3").build();
        this.objectMapper = objectMapper;
        this.sessionRepository = sessionRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.teacherRepository = teacherRepository;
        this.googleRefreshTokenService = googleRefreshTokenService;
        this.googleTokenService = googleTokenService;
    }

//    @Override
//    public String createSession(SessionRequest sessionRequest) {
//
//        SchoolClass schoolClass = classRepository.findById(sessionRequest.getSchoolClass().getClassId()).orElseThrow(() ->
//                new ResourceNotFoundException("School class with ID: " + sessionRequest.getSchoolClass().getClassId() + " not found in database."));
//
//        Section section = sectionRepository.findById(sessionRequest.getSection().getSectionId()).orElseThrow(() ->
//                new ResourceNotFoundException("Section with ID: " + sessionRequest.getSection().getSectionId() + " not found in database."));
//
//        Teacher teacher = teacherRepository.findById(sessionRequest.getTeacher().getId()).orElseThrow(() ->
//                new ResourceNotFoundException("Teacher with ID: " + sessionRequest.getTeacher().getId() + " not found in database."));
//
//        Session session = Session.builder()
//                .schoolClass(schoolClass)
//                .section(section)
//                .teacher(teacher)
//                .date(sessionRequest.getDate())
//                .startTime(sessionRequest.getStartTime())
//                .endTime(sessionRequest.getEndTime())
//                .topic(sessionRequest.getTopic())
//                .description(sessionRequest.getDescription())
//                .build();
//
//        sessionRepository.save(session);
//
//        return "Session saved successfully";
//    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Session createSession(Session session) {
        try {
           return sessionRepository.save(session);
        }catch(RuntimeException e){
            return null;
        }
    }

    @Override
    public List<Session> getTeacherSessions(Long id) {
        return sessionRepository.findAllByTeacher_Id(id);
    }

    @Override
    public List<Session> getStudentSessions(Long sectionId) {
        return sessionRepository.findAllBySection_SectionId(sectionId);
    }

    @Override
    public String addJoinLink(Long sessionId) throws IOException {

        Session session = sessionRepository.findById(sessionId).orElseThrow(() ->
                new ResourceNotFoundException("Session record with sessionId: " + sessionId
                        + " not found in database."));

        String refreshToken = googleRefreshTokenService.getRefreshToken(session.getTeacher().getId());
        if(refreshToken==null) {
            return "Teacher has not connected to Google yet. Please login first.";
        }

        String accessTokenFromRefreshToken = googleTokenService.getAccessTokenFromRefreshToken(refreshToken);

        String meetLink = createMeetLink(accessTokenFromRefreshToken, session.getTopic(), session.getDescription(),
                session.getStartTime(), session.getEndTime(), "Asia/Kolkata");

        session.setJoinLink(meetLink);

        sessionRepository.save(session);

        return "Session joinLink: " + meetLink + " added successfully.";
    }

    public String createMeetLink(String accessToken,
                                 String title,
                                 String description,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime,
                                 String timeZone) {
        try {
            // Convert time to RFC3339 format required by Google API
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            String start = startTime.atZone(java.time.ZoneId.of(timeZone)).format(formatter);
            String end = endTime.atZone(java.time.ZoneId.of(timeZone)).format(formatter);

            // Unique request ID for Meet link creation
            String requestId = UUID.randomUUID().toString();

            // Build request JSON
            String body = """
            {
              "summary": "%s",
              "description": "%s",
              "start": { "dateTime": "%s", "timeZone": "%s" },
              "end": { "dateTime": "%s", "timeZone": "%s" },
              "conferenceData": {
                "createRequest": {
                  "requestId": "%s"
                }
              }
            }
            """.formatted(title, description, start, timeZone, end, timeZone, requestId);

            // Call Google Calendar API
            String response = webClient.post()
                    .uri("/calendars/primary/events?conferenceDataVersion=1")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Extract hangoutLink from response
            JsonNode json = objectMapper.readTree(response);
            return json.has("hangoutLink")
                    ? json.get("hangoutLink").asText()
                    : "No Meet link created. Response: " + response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Google Meet link", e);
        }
    }
}
