package com.digital.dto;

import com.digital.entity.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long studentRegId;
    private String rollNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private LocalDate dateOfBirth;
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

    private Long feeId;

    private LocalDateTime enrolledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String username;

    public static StudentResponse fromEntity(Student student) {
        return StudentResponse.builder()
                .studentRegId(student.getStudentRegId())
                .rollNumber(student.getRollNumber())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .mobileNumber(student.getMobileNumber())
                .dateOfBirth(student.getDateOfBirth())
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
                .feeId(student.getFeeStructure() != null ? student.getFeeStructure().getFeeId() : null) // ✅ set feeId
                .enrolledAt(student.getEnrolledAt())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .username(student.getUser() != null ? student.getUser().getUsername() : null)
                .build();
    }
}
