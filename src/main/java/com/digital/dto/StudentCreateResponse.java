package com.digital.dto;

import com.digital.entity.Student;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCreateResponse {
    private Long studentRegId;
    private String rollNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String dateOfBirth;
    private String gender;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pinCode;
    private Long classId;
    private String className;
    private Long sectionId;
    private String sectionName;
    private LocalDateTime enrolledAt;  // only shown after enrollment
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;

    public static StudentCreateResponse fromEntity(Student student) {
        return StudentCreateResponse.builder()
                .studentRegId(student.getStudentRegId())
                .rollNumber(student.getRollNumber())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .mobileNumber(student.getMobileNumber())
                .dateOfBirth(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : null)

                .gender(student.getGender())
                .street(student.getStreet())
                .city(student.getCity())
                .state(student.getState())
                .country(student.getCountry())
                .pinCode(student.getPinCode())
                .classId(student.getSchoolClass() != null ? student.getSchoolClass().getClassId() : null)
                .className(student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : null)
                .sectionId(student.getSection() != null ? student.getSection().getSectionId() : null)
                .sectionName(student.getSection() != null ? student.getSection().getSectionName() : null)
                .enrolledAt(student.getEnrolledAt())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .username(student.getUser() != null ? student.getUser().getUsername() : null)
                .build();
    }
}
