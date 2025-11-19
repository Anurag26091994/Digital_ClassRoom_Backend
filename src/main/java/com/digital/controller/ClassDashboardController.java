package com.digital.controller;





import com.digital.dto.AssignedTeacherResponse;
import com.digital.dto.StudentResponse;
import com.digital.dto.AssignedTeacherResponse;
import com.digital.entity.Teacher;
import com.digital.entity.Student;
import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import com.digital.servicei.StudentService;
import com.digital.servicei.TeacherService;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class ClassDashboardController {

    private final TeacherService teacherService;
    private final StudentService studentService;

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/teachers/me/classes")
    public ResponseEntity<List<AssignedTeacherResponse>> getTeacherClasses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // logged-in user's username (email)

        // Use the injected service instance, not the class
        Teacher teacher = teacherService.getTeacherByUsername(username);
        Long teacherId = teacher.getId();

        List<AssignedTeacherResponse> assignedClasses = teacherService.getAssignedClassesForTeacher(teacherId);
        return ResponseEntity.ok(assignedClasses);
    }


    @GetMapping("/students/me/class")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponse> getStudentClass() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // logged-in user's username (from User table)

        // Fetch Student via the service
        Student student = studentService.getStudentByUsername(username);
        Long studentId = student.getStudentRegId();

        StudentResponse studentClass = studentService.getStudentClassDetails(studentId);
        return ResponseEntity.ok(studentClass);
    }

}
