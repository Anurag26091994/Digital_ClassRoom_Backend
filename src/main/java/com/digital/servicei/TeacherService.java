package com.digital.servicei;

//import com.digital.dto.AssignTeacherRequest;
//import com.digital.dto.AssignedTeacherResponse;
import com.digital.dto.AssignTeacherRequest;
import com.digital.dto.AssignedTeacherResponse;
import com.digital.dto.TeacherCreateRequest;
import com.digital.dto.TeacherDto;
import com.digital.entity.Teacher;

import java.util.List;

public interface TeacherService {
   public TeacherDto createTeacher(TeacherCreateRequest request);
    TeacherDto getTeacherById(Long id);
    List<TeacherDto> getAllTeachers();
    TeacherDto updateTeacher(Long id, Teacher teacher);
    String deleteTeacher(Long id);
    //    @Override
//public List<AssignedTeacherResponse> getAssignedClassesForTeacher(Long teacherId) {
//        List<ClassTeacher> assignments = classTeacherRepository.findByTeacherId(teacherId);
//
//        return assignments.stream().map(mapping -> {
//            SchoolClass schoolClass = classRepository.findById(mapping.getClassId()).orElse(null);
//            Section section = sectionRepository.findById(mapping.getSectionId()).orElse(null);
//            Teacher teacher = mapping.getTeacher(); // fetch teacher entity
//
//            return AssignedTeacherResponse.builder()
//                    .id(mapping.getId())
//                    .classId(mapping.getClassId())
//                    .className(schoolClass != null ? schoolClass.getClassName() : null)
//                    .sectionId(mapping.getSectionId())
//                    .sectionName(section != null ? section.getSectionName() : null)
//                    .teacherId(teacher != null ? teacher.getId() : null) // set teacherId
//                    .teacherName(teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : null) // set teacherName
//                    .assignedAt(mapping.getAssignedAt())
//                    .build();
//        }).collect(Collectors.toList());
//    }
//    @Override
//    public Teacher getTeacherByUsername(String username) {
//        return teacherRepository.findByUserUsername(username)
//                .orElseThrow(() -> new RuntimeException("Teacher not found with username: " + username));
//    }
//    TeacherDto createTeacher(TeacherCreateRequest request);
//    AssignedTeacherResponse assignTeacher(Long classId, Long sectionId, AssignTeacherRequest request);
//    List<AssignedTeacherResponse> getAssignedTeachers(Long classId);
//    List<AssignedTeacherResponse> getAssignedClassesForTeacher(Long teacherId);
//    Teacher getTeacherByUsername(String username);
    public List<AssignedTeacherResponse> getAssignedClassesForTeacher(Long teacherId);
    public Teacher getTeacherByUsername(String username);
    AssignedTeacherResponse assignTeacher(Long classId, Long sectionId, AssignTeacherRequest request);

    List<AssignedTeacherResponse> getAssignedTeachers(Long classId);

}
