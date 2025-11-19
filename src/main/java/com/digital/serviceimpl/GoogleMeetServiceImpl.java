//package com.digital.serviceimpl;
//
//import com.digital.servicei.GoogleMeetService;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.*;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.security.GeneralSecurityException;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//@Service
//public class GoogleMeetServiceImpl implements GoogleMeetService {
//
//    private static final String APPLICATION_NAME = "School PTM Scheduler";
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar");
//    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//
//    private Calendar calendarService;
//
//    @PostConstruct
//    public void init() {
//        try {
//            calendarService = createCalendarService();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to initialize Google Calendar service", e);
//        }
//    }
//
//    private Calendar createCalendarService() throws IOException, GeneralSecurityException {
//        InputStream in = getClass().getResourceAsStream("/credentials.json");
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: credentials.json");
//        }
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JSON_FACTORY,
//                clientSecrets,
//                SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//
//        return new Calendar.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JSON_FACTORY,
//                new AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//        )
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    @Override
//    public String createGoogleMeetLink(String title, LocalDateTime meetingDateTime, int durationMinutes)
//            throws GeneralSecurityException, IOException {
//
//        if (calendarService == null) {
//            throw new IllegalStateException("Google Calendar service not initialized");
//        }
//
//        Event event = new Event()
//                .setSummary(title)
//                .setDescription("Parent-Teacher Meeting");
//
//        Date startDate = Date.from(meetingDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        Date endDate = Date.from(meetingDateTime.plusMinutes(durationMinutes).atZone(ZoneId.systemDefault()).toInstant());
//
//        event.setStart(new EventDateTime().setDateTime(new DateTime(startDate)));
//        event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));
//
//        // Add Google Meet conference
//        ConferenceData conferenceData = new ConferenceData()
//                .setCreateRequest(new CreateConferenceRequest()
//                        .setRequestId("ptm-" + System.currentTimeMillis())
//                        .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet")));
//
//        event.setConferenceData(conferenceData);
//
//        // Insert event with conferenceDataVersion=1
//        Event createdEvent = calendarService.events().insert("primary", event)
//                .setConferenceDataVersion(1)
//                .execute();
//
//        if (createdEvent.getHangoutLink() == null) {
//            throw new RuntimeException("Google Meet link could not be created");
//        }
//
//        return createdEvent.getHangoutLink();
//    }
//}
package com.digital.serviceimpl;
import com.digital.google_meet_config.GoogleRefreshToken;
import com.digital.google_meet_config.GoogleTokenService;
import com.digital.repository.GoogleRefreshTokenRepository;
import com.digital.servicei.GoogleMeetService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class GoogleMeetServiceImpl implements GoogleMeetService {

    private static final String APPLICATION_NAME = "School PTM Scheduler";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Autowired
    private GoogleTokenService googleTokenService;

    @Autowired
    private GoogleRefreshTokenRepository tokenRepository;

    private Calendar createCalendarService(Long teacherId) throws IOException, GeneralSecurityException {
        GoogleRefreshToken token = tokenRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new RuntimeException("No refresh token found for teacherId: " + teacherId));

        String accessToken = googleTokenService.getAccessTokenFromRefreshToken(token.getRefreshToken());

        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public String createGoogleMeetLink(Long teacherId, String title, LocalDateTime meetingDateTime, int durationMinutes)
            throws GeneralSecurityException, IOException {

        Calendar calendarService = createCalendarService(teacherId);

        Event event = new Event()
                .setSummary(title)
                .setDescription("Parent-Teacher Meeting");

        Date startDate = Date.from(meetingDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(meetingDateTime.plusMinutes(durationMinutes).atZone(ZoneId.systemDefault()).toInstant());

        event.setStart(new EventDateTime().setDateTime(new DateTime(startDate)));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));

        // Enable Meet link creation
        ConferenceData conferenceData = new ConferenceData()
                .setCreateRequest(new CreateConferenceRequest()
                        .setRequestId("ptm-" + System.currentTimeMillis())
                        .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet")));
        event.setConferenceData(conferenceData);

        Event createdEvent = calendarService.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        if (createdEvent.getHangoutLink() == null) {
            throw new RuntimeException("Failed to create Google Meet link.");
        }

        return createdEvent.getHangoutLink();
    }
}
