package com.digital.serviceimpl;

import com.digital.dto.TimetableRequest;
import com.digital.dto.UpdateTimetableRequest;
import com.digital.entity.*;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.TimetableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final SessionRepository sessionRepository;

    private final SessionServiceImpl sessionService;

    @Override
    public String createTimetable(TimetableRequest timetableRequest) {

        SchoolClass schoolClass = classRepository.findById(timetableRequest.getSchoolClass().getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class with id: "
                        + timetableRequest.getSchoolClass().getClassId() + " not present in database."));

        Section section = sectionRepository.findById(timetableRequest.getSection().getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section with id: " +
                        timetableRequest.getSection().getSectionId() + " not present in database."));

        Subject subject = subjectRepository.findById(timetableRequest.getSubject().getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject with id: "
                        + timetableRequest.getSubject().getSubjectId() + " not present in database."));

        Teacher teacher = teacherRepository.findById(timetableRequest.getTeacher().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher with id: "
                        + timetableRequest.getTeacher().getId() + " not present in database."));

        if(timetableRepository.existsBySection_SectionIdAndStartTime(section.getSectionId(), timetableRequest.getStartTime())){
            return "Failed to create timetable as timetable with section id: " + section.getSectionId() + " and " + "start time "
                    + timetableRequest.getStartTime() + " is already present in database.";
        }

        if(timetableRepository.existsByStartTimeAndTeacher_Id(timetableRequest.getStartTime(), teacher.getId())){
            return "Failed to create timetable as timetable with start time: " + timetableRequest.getStartTime() +
                    " and teacher with id: " + teacher.getId() + " is already present in database";
        }

        if(!(timetableRequest.getStartTime().isBefore(timetableRequest.getEndTime()))){
            return "End time should be greater than start time.";
        }

        Timetable timetable = Timetable.builder()
                .schoolClass(schoolClass)
                .section(section)
                .subject(subject)
                .teacher(teacher)
                .date(timetableRequest.getDate())
                .dayOfWeek(timetableRequest.getDayOfWeek())
                .startTime(timetableRequest.getStartTime())
                .endTime(timetableRequest.getEndTime())
                .build();

            Timetable savedTimetable = timetableRepository.save(timetable);

            Session session = Session.builder()
                    .timetable(timetable)
                    .schoolClass(schoolClass)
                    .section(section)
                    .teacher(teacher)
                    .date(timetable.getDate())
                    .startTime(timetable.getStartTime())
                    .endTime(timetable.getEndTime())
                    .topic(timetableRequest.getTopic())
                    .description(timetableRequest.getDescription())
                    .build();

                Session savedSession = sessionService.createSession(session);
                if(savedSession!=null) {
                    return "Timetable and Session created successfully";
                }else
                    return "Timetable created but Session failed to create";
    }

    @Override
    public String updateTimetable(Long timetableId, UpdateTimetableRequest request) {

        Timetable existingTimetable = timetableRepository.findById(timetableId).orElseThrow(() ->
                new ResourceNotFoundException("Timetable record with id: " + timetableId + " not found in database"));

        if(LocalDateTime.now().isAfter(existingTimetable.getStartTime())){
            return "Failed to update timetable and related sessions as only future timetable records can be updated.";
        }


        Teacher teacher = teacherRepository.findById(request.getTeacher().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Teacher record with id: " + request.getTeacher().getId() +
                        " not found in database"));

        Subject subject = subjectRepository.findById(request.getSubject().getSubjectId()).orElseThrow(() ->
                new ResourceNotFoundException("Subject record with id: " + request.getSubject().getSubjectId() +
                        " not found in database"));

        existingTimetable.setTeacher(teacher);
        existingTimetable.setSubject(subject);
        existingTimetable.setStartTime(request.getStartTime());
        existingTimetable.setEndTime(request.getEndTime());
        existingTimetable.setDate(request.getDate());

        timetableRepository.save(existingTimetable);

        Session existingSession = sessionRepository.findByTimetable_TimetableId(timetableId);

        existingSession.setTeacher(teacher);
        existingSession.setTopic(request.getTopic());
        existingSession.setDescription(request.getDescription());
        existingSession.setStartTime(existingTimetable.getStartTime());
        existingSession.setEndTime(existingTimetable.getEndTime());

        sessionRepository.save(existingSession);

        return "Timetable with id: " + timetableId + " and " + " Session with id: " +
                existingSession.getSessionId() + " updated successfully. Now add new joinLink to that session";
    }

    @Override
    public List<Timetable> getTimetables() {

        return timetableRepository.findAll();

//        List<TimetableResponse> list = new ArrayList<>();
//
//        timetables.forEach(t -> {
//            TimetableResponse build = TimetableResponse.builder()
//                    .schoolClassId(t.getSchoolClass().getClassId())
//                    .subjectId(t.getSubject().getSubjectId())
//                    .sectionId(t.getSection().getSectionId())
//                    .teacherId(t.getTeacher().getId())
//                    .createdAt(t.getCreatedAt())
//                    .updatedAt(t.getUpdatedAt())
//                    .startTime(t.getStartTime())
//                    .endTime(t.getEndTime())
//                    .date(t.getDate())
//                    .dayOfWeek(t.getDayOfWeek())
//                    .build();
//
//            list.add(build);
//        });
//        return list;
    }

    @Override
    public String deleteTimetable(Long timetableId) {

        Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() ->
                new ResourceNotFoundException("Timetable record with id: " + timetableId + " not found in database"));

        Session session = sessionRepository.findByTimetable_TimetableId(timetableId);

        if(LocalDateTime.now().isAfter(timetable.getStartTime())){
            return "Failed to delete timetable and related sessions as only future timetable records can be deleted.";
        }

        sessionRepository.delete(session);

        timetableRepository.delete(timetable);

        return "Timetable record with id: " + timetableId + " and related session with id: " + session.getSessionId()
                + " deleted successfully.";
    }

    @Override
    public List<Timetable> getStudentTimetableBySectionId(Long sectionId) {
        return timetableRepository.findAllBySection_SectionId(sectionId);
    }
}
