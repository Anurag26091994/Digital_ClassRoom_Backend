package com.digital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class FeeStructureDTO {

    private Long feeId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year must be at most 20 characters")
    private String academicYear;

    @NotNull(message = "Tuition fee cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Tuition fee cannot be negative")
    private BigDecimal tuitionFee = BigDecimal.ZERO;

    @NotNull(message = "Exam fee cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Exam fee cannot be negative")
    private BigDecimal examFee = BigDecimal.ZERO;

    @NotNull(message = "Transport fee cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Transport fee cannot be negative")
    private BigDecimal transportFee = BigDecimal.ZERO;

    @NotNull(message = "Library fee cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Library fee cannot be negative")
    private BigDecimal libraryFee = BigDecimal.ZERO;

    @NotNull(message = "Other charges cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Other charges cannot be negative")
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", inclusive = true, message = "Total amount cannot be negative")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Long getFeeId() { return feeId; }
    public void setFeeId(Long feeId) { this.feeId = feeId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }

    public BigDecimal getExamFee() { return examFee; }
    public void setExamFee(BigDecimal examFee) { this.examFee = examFee; }

    public BigDecimal getTransportFee() { return transportFee; }
    public void setTransportFee(BigDecimal transportFee) { this.transportFee = transportFee; }

    public BigDecimal getLibraryFee() { return libraryFee; }
    public void setLibraryFee(BigDecimal libraryFee) { this.libraryFee = libraryFee; }

    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
