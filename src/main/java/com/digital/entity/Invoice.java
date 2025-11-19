package com.digital.entity;

import com.digital.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices",
        indexes = {@Index(columnList = "student_id"), @Index(columnList = "fee_id")})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private FeeStructure feeStructure;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal totalDue = BigDecimal.ZERO;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @PastOrPresent
    private Instant generatedAt;

    @PastOrPresent
    private Instant updatedAt;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.generatedAt = now;
        this.updatedAt = now;
        if (this.amountPaid == null) this.amountPaid = BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public void recomputeStatus() {
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        if (totalDue == null) totalDue = BigDecimal.ZERO;

        int cmp = amountPaid.compareTo(totalDue);
        if (cmp >= 0) {
            this.status = InvoiceStatus.PAID;
            this.amountPaid = totalDue;
        } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        } else {
            if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
                this.status = InvoiceStatus.OVERDUE;
            } else {
                this.status = InvoiceStatus.UNPAID;
            }
        }
    }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public FeeStructure getFeeStructure() { return feeStructure; }
    public void setFeeStructure(FeeStructure feeStructure) { this.feeStructure = feeStructure; }

    public BigDecimal getTotalDue() { return totalDue; }
    public void setTotalDue(BigDecimal totalDue) { this.totalDue = totalDue; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}
