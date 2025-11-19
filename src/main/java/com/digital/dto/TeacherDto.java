//package com.digital.dto;
//
//import lombok.Builder;
//import lombok.Data;
//
//import java.util.List;
//
//@Data
//@Builder
//
//public class TeacherDto {
////    private String firstName;
////    private String lastName;
////    private String email;
////    private String phone;
////    private String qualification;
////    private Integer experienceYears;
////    private String gender;
////    //private String assignedClasses;
////    private String dateOfBirth;
////    private Long userId; // for user reference
////
////    private List<Long> assignedClassIds; // list of class IDs
////    private List<Long> assignedSectionIds; // list of section IDs
////    private List<Long> studentIds; // list of student IDs
//private Long userId;
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String phone;
//    private String qualification;
//    private Integer experienceYears;
//    private String gender;
//    private String dateOfBirth;
//    private List<Long> classIds;
//    private List<String> classNames;
//    private List<Long> sectionIds;
//    private List<String> sectionNames;
//
//    // getters and setters
//}
package com.digital.dto;

import com.digital.entity.ClassTeacher;
import com.digital.entity.Section;
import com.digital.entity.SchoolClass;
import com.digital.entity.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private Integer experienceYears;
    private String gender;
    private String dateOfBirth;
    private List<Long> classIds;
    private List<String> classNames;
    private List<Long> sectionIds;
    private List<String> sectionNames;
    private boolean assignedAsClassTeacher;;

    private Long classTeacherId;


    // Constructor to map Teacher entity to DTO
    public TeacherDto(Teacher teacher) {
        this.userId = teacher.getUser() != null ? teacher.getUser().getUserId() : null;
        this.firstName = teacher.getFirstName();
        this.lastName = teacher.getLastName();
        this.email = teacher.getEmail();
        this.phone = teacher.getPhone();
        this.qualification = teacher.getQualification();
        this.experienceYears = teacher.getExperienceYears();
        this.gender = teacher.getGender();
        this.dateOfBirth = teacher.getDateOfBirth();

        if (teacher.getAssignedClass() != null) {
            this.classIds = teacher.getAssignedClass().stream()
                    .map(SchoolClass::getClassId)
                    .collect(Collectors.toList());
            this.classNames = teacher.getAssignedClass().stream()
                    .map(SchoolClass::getClassName)
                    .collect(Collectors.toList());
        }

        if (teacher.getAssignedSection() != null) {
            this.sectionIds = teacher.getAssignedSection().stream()
                    .map(Section::getSectionId)
                    .collect(Collectors.toList());
            this.sectionNames = teacher.getAssignedSection().stream()
                    .map(Section::getSectionName)
                    .collect(Collectors.toList());
        }
    }
}
