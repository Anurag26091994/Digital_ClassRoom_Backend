package com.digital.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeacherCreateRequest {
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String phone;
//    private String qualification;
//    private Integer experienceYears;
//    private String gender;
//    private String dateOfBirth;
//    private Long userId;

//    private List<Long> assignedClassIds;
//    private List<Long> assignedSectionIds;
//    private List<Long> studentIds;
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String qualification;

    private Integer experienceYears;

    private String gender;

    private String dateOfBirth;

    private List<Long> assignedClassIds;

    private List<Long> assignedSectionIds;

    private boolean assignedAsClassTeacher;

    private Long classId;

    private Long sectionId;
}


