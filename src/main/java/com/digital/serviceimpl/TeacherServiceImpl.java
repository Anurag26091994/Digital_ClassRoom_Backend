
package com.digital.serviceimpl;

import com.digital.dto.AssignTeacherRequest;
import com.digital.dto.AssignedTeacherResponse;
import com.digital.dto.TeacherCreateRequest;
import com.digital.dto.TeacherDto;
import com.digital.entity.*;
import com.digital.repository.*;
import com.digital.servicei.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClassTeacherRepository classTeacherRepository;


    private TeacherDto mapToDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .userId(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .qualification(teacher.getQualification())
                .experienceYears(teacher.getExperienceYears())
                .gender(teacher.getGender())
                .dateOfBirth(teacher.getDateOfBirth())
                .classTeacherId(teacher.getClassTeacher().getClassTeacherId())
                .assignedAsClassTeacher(teacher.isAssignedAsClassTeacher())
                .classIds(teacher.getAssignedClass() != null
                        ? teacher.getAssignedClass().stream().map(SchoolClass::getClassId).toList()
                        : List.of())
                .classNames(teacher.getAssignedClass() != null
                        ? teacher.getAssignedClass().stream().map(SchoolClass::getClassName).toList()
                        : List.of())
                .sectionIds(teacher.getAssignedSection() != null
                        ? teacher.getAssignedSection().stream().map(Section::getSectionId).toList()
                        : List.of())
                .sectionNames(teacher.getAssignedSection() != null
                        ? teacher.getAssignedSection().stream().map(Section::getSectionName).toList()
                        : List.of())
                .build();
    }

    // Map Teacher entity to AssignedTeacherResponse
    public AssignedTeacherResponse mapToAssignedResponse(Teacher teacher) {
        return AssignedTeacherResponse.builder()
                .id(teacher.getId())
                .teacherId(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())


                .classId(!teacher.getAssignedClass().isEmpty() ? teacher.getAssignedClass().get(0).getClassId() : null)
                .className(!teacher.getAssignedClass().isEmpty() ?
                        teacher.getAssignedClass().stream().map(SchoolClass::getClassName).toList() : List.of())
                .sectionId(!teacher.getAssignedSection().isEmpty() ? teacher.getAssignedSection().get(0).getSectionId() : null)
                .sectionName(!teacher.getAssignedSection().isEmpty() ?
                        teacher.getAssignedSection().stream().map(Section::getSectionName).toList() : List.of())
                .assignedAt(teacher.getAssignedAt())
                .build();
    }

    // Fetch all teachers
    public List<AssignedTeacherResponse> getAllAssignedTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(this::mapToAssignedResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDto getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    @Override
    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDto updateTeacher(Long id, Teacher teacher) {
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        existing.setFirstName(teacher.getFirstName());
        existing.setLastName(teacher.getLastName());
        existing.setEmail(teacher.getEmail());
        existing.setPhone(teacher.getPhone());
        existing.setQualification(teacher.getQualification());
        existing.setExperienceYears(teacher.getExperienceYears());
        existing.setGender(teacher.getGender());
        existing.setDateOfBirth(teacher.getDateOfBirth());

        return mapToDto(teacherRepository.save(existing));
    }

    @Override
    public String deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
        return "Teacher with ID " + id + " has been successfully deleted.";
    }

    @Override
    public AssignedTeacherResponse assignTeacher(Long classId, Long sectionId, AssignTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + request.getTeacherId()));

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with ID: " + classId));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found with ID: " + sectionId));

        // ✅ Add to ManyToMany lists
        teacher.getAssignedClass().add(schoolClass);
        teacher.getAssignedSection().add(section);
        teacher.setAssignedAt(LocalDateTime.now());
        teacherRepository.save(teacher);

        return mapToAssignedResponse(teacher);
    }

    @Override
    public List<AssignedTeacherResponse> getAssignedTeachers(Long classId) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with ID: " + classId));

        return schoolClass.getSections().stream()
                .flatMap(section -> section.getTeachers().stream()
                        .map(this::mapToAssignedResponse))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignedTeacherResponse> getAssignedClassesForTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + teacherId));

        return List.of(mapToAssignedResponse(teacher));
    }

    @Override
    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found with username: " + username));
    }

//    @Override
//    public TeacherDto createTeacher(TeacherCreateRequest request) {
//
//        // ✅ 1. Load existing User
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));
//
//        // ✅ 2. Prepare Teacher entity
//        Teacher.TeacherBuilder builder = Teacher.builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .phone(request.getPhone())
//                .qualification(request.getQualification())
//                .experienceYears(request.getExperienceYears())
//                .gender(request.getGender())
//                .dateOfBirth(request.getDateOfBirth())
//                .user(user)
//                .assignedAt(LocalDateTime.now())
//
//                .isClassTeacher(request.isClassTeacher());
//
//
//
//        // ✅ 3. Handle ClassTeacher assignment if applicable
//        if (request.isClassTeacher()) {
//            if (request.getClassTeacherId() != null) {
//                // Load existing ClassTeacher
//                ClassTeacher classTeacher = classTeacherRepository.findById(request.getClassTeacherId())
//                        .orElseThrow(() -> new RuntimeException("ClassTeacher not found with ID: " + request.getClassTeacherId()));
//                builder.classTeacher(classTeacher);
//            } else if (request.getClassId() != null && request.getSectionId() != null) {
//                // Create new ClassTeacher if ID not provided
//                SchoolClass schoolClass = classRepository.findById(request.getClassId())
//                        .orElseThrow(() -> new RuntimeException("Class not found with ID: " + request.getClassId()));
//                Section section = sectionRepository.findById(request.getSectionId())
//                        .orElseThrow(() -> new RuntimeException("Section not found with ID: " + request.getSectionId()));
//
//                ClassTeacher newClassTeacher = ClassTeacher.builder()
//                        .schoolClass(schoolClass)
//                        .section(section)
//                        .assignedAt(LocalDateTime.now())
//                        .build();
//
//                classTeacherRepository.save(newClassTeacher); // Persist new ClassTeacher
//                builder.classTeacher(newClassTeacher);
//            } else {
//                throw new RuntimeException("To assign as ClassTeacher, either classTeacherId or both classId and sectionId must be provided.");
//            }
//        }
//
//        // ✅ 4. Build teacher
//        Teacher teacher = builder.build();
//
//        // ✅ 5. Assign classes
//        if (request.getAssignedClassIds() != null && !request.getAssignedClassIds().isEmpty()) {
//            List<SchoolClass> classes = classRepository.findAllById(request.getAssignedClassIds());
//            teacher.getAssignedClass().addAll(classes);
//        }
//
//        // ✅ 6. Assign sections
//        if (request.getAssignedSectionIds() != null && !request.getAssignedSectionIds().isEmpty()) {
//            List<Section> sections = sectionRepository.findAllById(request.getAssignedSectionIds());
//            teacher.getAssignedSection().addAll(sections);
//        }
//        ClassTeacher classTeacher = null;
//        if (request.isClassTeacher() && request.getClassTeacherId() != null) {
//            classTeacher = classTeacherRepository.findById(request.getClassTeacherId())
//                    .orElseThrow(() -> new RuntimeException("ClassTeacher not found with ID: " + request.getClassTeacherId()));
//        }
//
//
//        // ✅ 7. Save and return DTO
//        Teacher savedTeacher = teacherRepository.save(teacher);
//        return mapToDto(savedTeacher);
//    }
//


    @Override
    public TeacherDto createTeacher(TeacherCreateRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getEmail()));

        //IsClassTeacher
        SchoolClass schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("class not found by get ID " + request.getClassId()));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("section not found by get ID " + request.getSectionId()));

        ClassTeacher build = ClassTeacher.builder().schoolClass(schoolClass).section(section).build();
        classTeacherRepository.save(build);
        Teacher teacher = Teacher.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .qualification(request.getQualification())
                .experienceYears(request.getExperienceYears())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .user(user)
                .assignedAsClassTeacher(request.isAssignedAsClassTeacher())
                .classTeacher(build)
                .assignedAt(LocalDateTime.now())

                .build();

        // Assign Classes
        if (request.getAssignedClassIds() != null && !request.getAssignedClassIds().isEmpty()) {
            List<SchoolClass> classes = classRepository.findAllById(request.getAssignedClassIds());
            teacher.getAssignedClass().addAll(classes);
        }

        // Assign Sections
        if (request.getAssignedSectionIds() != null && !request.getAssignedSectionIds().isEmpty()) {
            List<Section> sections = sectionRepository.findAllById(request.getAssignedSectionIds());
            teacher.getAssignedSection().addAll(sections);
        }



        // Save teacher (ID will be generated here)
        Teacher savedTeacher = teacherRepository.save(teacher);
        System.out.println("Saved Teacher ID: " + savedTeacher.getId()); // ✅ Debug: confirm ID

        // Convert to DTO
        return mapToDto(savedTeacher);
    }

}