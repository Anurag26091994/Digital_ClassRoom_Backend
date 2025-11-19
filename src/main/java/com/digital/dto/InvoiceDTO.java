package com.digital.dto;

import com.digital.enums.InvoiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private Long invoiceId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Fee ID is required")
    private Long feeId;

    @NotNull(message = "Total due is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Total due cannot be negative")
    private BigDecimal totalDue;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Status is required")
    private InvoiceStatus status;

    @DecimalMin(value = "0.00", inclusive = true, message = "Amount paid cannot be negative")
    private BigDecimal amountPaid = BigDecimal.ZERO;
}
