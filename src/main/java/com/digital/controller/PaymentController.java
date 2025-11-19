package com.digital.controller;

import com.digital.dto.PaymentDTO;
import com.digital.servicei.MockGatewayService;
import com.digital.servicei.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final MockGatewayService mockGatewayService;

    public PaymentController(PaymentService paymentService, MockGatewayService mockGatewayService) {
        this.paymentService = paymentService;
        this.mockGatewayService = mockGatewayService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/pay")
    public ResponseEntity<PaymentDTO> pay(@Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.status(201).body(paymentService.makePayment(dto));
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student")
    public ResponseEntity<List<PaymentDTO>> byStudent() {
        List<PaymentDTO> payments = paymentService.getPaymentsByStudent();
        return ResponseEntity.ok(payments);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> updatePayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentDTO dto) {
        PaymentDTO updated = paymentService.updatePayment(paymentId, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok("Payment with ID " + paymentId + " deleted successfully.");
    }
}
