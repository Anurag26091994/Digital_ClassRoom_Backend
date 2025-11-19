package com.digital.servicei;

import com.digital.entity.Session;

import java.io.IOException;
import java.util.List;

public interface SessionService {

    //String createSession(SessionRequest sessionRequest);

    List<Session> getAllSessions();

    Session createSession(Session session);

    String addJoinLink(Long sessionId) throws IOException;

    List<Session> getTeacherSessions(Long id);

    List<Session> getStudentSessions(Long sectionId);

    // create session

    // view sessions by class and section

    // view session by sessionId

    // view sessions

}
