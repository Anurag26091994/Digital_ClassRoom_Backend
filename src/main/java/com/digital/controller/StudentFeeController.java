package com.digital.controller;

import com.digital.dto.FeeStructureDTO;
import com.digital.dto.InvoiceDTO;
import com.digital.dto.PaymentDTO;
import com.digital.servicei.FeeService;
import com.digital.servicei.InvoiceService;
import com.digital.servicei.PaymentService;
import com.digital.servicei.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studentFee")
@CrossOrigin(origins = "*")
public class StudentFeeController {

    private final FeeService feeService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final ReceiptService receiptService;

    public StudentFeeController(FeeService feeService,
                                InvoiceService invoiceService,
                                PaymentService paymentService,
                                ReceiptService receiptService) {
        this.feeService = feeService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.receiptService = receiptService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/fees")
    public ResponseEntity<List<FeeStructureDTO>> getAllFeeStructures() {
        return ResponseEntity.ok(feeService.getAllFeeStructures());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTO>> getInvoices() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(invoiceService.getInvoicesByStudent(username));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/invoices/{id}/receipt")
    public ResponseEntity<InvoiceDTO> getReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/payments")
    public ResponseEntity<PaymentDTO> makePayment(@Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.makePayment(dto));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/payments/history")
    public ResponseEntity<List<PaymentDTO>> getPaymentHistory() {
        return ResponseEntity.ok(paymentService.getPaymentsByStudent());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/payments/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, dto));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/invoices/all")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }


    // ===================== RECEIPT DOWNLOAD =====================
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/invoices/{invoiceId}/download-receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long invoiceId) {
        try {
            byte[] pdfBytes = receiptService.generateReceipt(invoiceId);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=receipt_" + invoiceId + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
