package com.digital.controller;

import com.digital.dto.FeeStructureDTO;
import com.digital.dto.InvoiceDTO;
import com.digital.dto.PaymentDTO;
import com.digital.servicei.FeeService;
import com.digital.servicei.InvoiceService;
import com.digital.servicei.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminFeeController {

    private final FeeService feeService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    public AdminFeeController(FeeService feeService,
                              InvoiceService invoiceService,
                              PaymentService paymentService) {
        this.feeService = feeService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/fees")
    public ResponseEntity<FeeStructureDTO> createFeeStructure(@Valid @RequestBody FeeStructureDTO dto) {
        return ResponseEntity.ok(feeService.createFeeStructure(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/fees/{id}")
    public ResponseEntity<FeeStructureDTO> updateFeeStructure(
            @PathVariable Long id,
            @Valid @RequestBody FeeStructureDTO dto) {
        return ResponseEntity.ok(feeService.updateFeeStructure(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/fees/{id}")
    public ResponseEntity<String> deleteFeeStructure(@PathVariable Long id) {
        feeService.deleteFeeStructure(id);
        return ResponseEntity.ok("Fee structure deleted successfully with id=" + id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/fees/{classId}")
    public ResponseEntity<List<FeeStructureDTO>> getFeeStructures(
            @PathVariable Long classId,
            @RequestParam(required = false) String year) {

        if (year != null) {
            return ResponseEntity.ok(feeService.getByClassAndYear(classId, year));
        }
        return ResponseEntity.ok(feeService.getByClass(classId));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceDTO> generateInvoice(
            @RequestParam Long studentId,
            @RequestParam Long feeId,
            @RequestParam String dueDate) {
        return ResponseEntity.ok(invoiceService.generateInvoice(studentId, feeId, dueDate));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(
            @PathVariable Long id,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String dueDate) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, studentId, dueDate));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/invoices/{id}/adjust")
    public ResponseEntity<InvoiceDTO> adjustInvoice(
            @PathVariable Long id,
            @RequestParam double adjustment) {
        return ResponseEntity.ok(invoiceService.applyAdjustment(id, adjustment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted successfully with id=" + id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getPaymentsByStudent());
    }
}
