package com.digital.servicei;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

public interface GoogleMeetService {

    String createGoogleMeetLink(Long teacherId, String title, LocalDateTime meetingDateTime, int durationMinutes)
            throws GeneralSecurityException, IOException;
}
