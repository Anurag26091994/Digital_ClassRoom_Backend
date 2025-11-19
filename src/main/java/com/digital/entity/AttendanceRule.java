package com.digital.entity;

import com.digital.enums.AttendanceRuleName;
import com.digital.enums.Operator;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class AttendanceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long attendanceRuleId;

    @NotNull(message = "Rule name is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceRuleName ruleName;

    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Operator is required")
    @Enumerated(EnumType.STRING)
    private Operator operator;  // This field is used to help ADMIN to enter session duration percentage values

    @NotNull(message = "Session Duration Percentage1 value is required")
    @Min(value = 0, message = "Min value must be >= 0")
    @Max(value = 100, message = "Max value must be <= 100")
    @Column(nullable = false)
    private Integer sessionDurationPercentage1;

    @Min(value = 0, message = "Min value must be >= 0")
    @Max(value = 100, message = "Max value must be <= 100")
    private Integer sessionDurationPercentage2; // This field is to represent range of values e.g 50 to 70%
}
