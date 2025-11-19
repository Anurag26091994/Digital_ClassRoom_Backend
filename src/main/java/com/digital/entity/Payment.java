package com.digital.entity;

import com.digital.enums.PaymentMode;

import com.digital.enums.PaymentStatus;

import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.PastOrPresent;

import lombok.*;

import java.math.BigDecimal;

import java.time.Instant;

import java.util.UUID;

@Entity

@Table(

        name = "payments",

        indexes = {

                @Index(columnList = "invoice_id"),

                @Index(columnList = "student_id")

        },

        uniqueConstraints = {

                @UniqueConstraint(columnNames = {"transaction_id"})

        }

)

@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Payment {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long paymentId;

    @NotNull

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "invoice_id", nullable = false)

    private Invoice invoice;

    @NotNull

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "student_id", nullable = false)

    private Student student;

    @NotNull

    @DecimalMin(value = "0.01", inclusive = true)

    private BigDecimal amountPaid;

    @NotNull

    @Enumerated(EnumType.STRING)

    private PaymentMode paymentMode;

    @Column(nullable = false, unique = true)

    private String transactionId;

    private String gatewayReferenceId;

    @PastOrPresent

    @Column(nullable = false)

    private Instant paymentDate;

    @NotNull

    @Enumerated(EnumType.STRING)

    private PaymentStatus status = PaymentStatus.PENDING;

    @PrePersist

    public void prePersist() {

        if (this.transactionId == null || this.transactionId.trim().isEmpty()) {

            this.transactionId = "txn_" + UUID.randomUUID();

        }

        if (this.paymentDate == null) {

            this.paymentDate = Instant.now();

        }

    }

    public Long getInvoiceId() {

        return invoice != null ? invoice.getInvoiceId() : null;

    }

    public Long getStudentId() {

        return student != null ? student.getStudentId() : null;

    }

    public void setInvoiceId(Long invoiceId) {

        if (this.invoice == null) {

            this.invoice = new Invoice();

        }

        this.invoice.setInvoiceId(invoiceId);

    }

    public void setStudentId(Long studentId) {

        if (this.student == null) {

            this.student = new Student();

        }

        this.student.setStudentId(studentId);

    }

}

