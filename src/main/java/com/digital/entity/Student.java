package com.digital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentRegId;  // primary key

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Roll number is required")
    @Pattern(regexp = "^[A-Z0-9]{5,15}$", message = "Roll number must be alphanumeric (5–15 characters)")
    private String rollNumber;

    @Column(nullable = false)
    @NotBlank(message = "Academic year is required")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{4}$", message = "Academic year must be in format YYYY-YYYY")
    private String academicYear;


    @Column(nullable = false, unique = true)
    @NotBlank(message = "Admission number is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Admission number must be 4–10 digits")
    private String admissionNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Pattern(regexp = "^[0-9]{10}$")
    private String mobileNumber;

    @Past
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(Male|Female|Other)$")
    private String gender;

    @Size(max = 100)
    private String street;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 50)
    private String country;

    @Pattern(regexp = "^[0-9]{5,10}$")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    private LocalDateTime enrolledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private FeeStructure feeStructure;

    // Relations
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // ✅ Convenience methods for Payment mapping
    public Long getStudentId() {
        return this.studentRegId;
    }

    public void setStudentId(Long studentId) {
        this.studentRegId = studentId;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    @ManyToMany(mappedBy = "students")
    private List<PTM> ptms;

}
