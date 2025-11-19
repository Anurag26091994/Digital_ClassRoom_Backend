package com.digital.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fee_structures",
        indexes = {@Index(columnList = "class_id, academic_year")})
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Long feeId;

    @NotNull
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal tuitionFee = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal examFee = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal transportFee = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal libraryFee = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @PastOrPresent
    private Instant createdAt;

    @PastOrPresent
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        recomputeTotal();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        recomputeTotal();
    }

    private void recomputeTotal() {
        this.totalAmount = (safe(tuitionFee)
                .add(safe(examFee))
                .add(safe(transportFee))
                .add(safe(libraryFee))
                .add(safe(otherCharges)));
    }

    private BigDecimal safe(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }

    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Invoice> invoices = new ArrayList<>();

    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public BigDecimal getExamFee() {
        return examFee;
    }

    public void setExamFee(BigDecimal examFee) {
        this.examFee = examFee;
    }

    public BigDecimal getTransportFee() {
        return transportFee;
    }

    public void setTransportFee(BigDecimal transportFee) {
        this.transportFee = transportFee;
    }

    public BigDecimal getLibraryFee() {
        return libraryFee;
    }

    public void setLibraryFee(BigDecimal libraryFee) {
        this.libraryFee = libraryFee;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}

