package com.digital.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeacherRequest {
    private String firstName;
    private String lastName;
    private Long id;
    private String subject;
    private String phone;
    private String email;
    private String Qualification;
    private  String gender;
    private String dateOfBirth;
    private String assignedClasses;
    private Integer experienceYears;
}

