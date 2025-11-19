package com.digital.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long paymentId;

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount paid must be positive")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    private String paymentMode;
    @Column(nullable = false,unique = true)
    private String transactionId;
    private String gatewayReferenceId;
    private String  paymentDate;
    private String paymentMethod;
    private String status;
}
