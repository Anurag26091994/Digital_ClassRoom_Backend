package com.digital.controller;

import com.digital.dto.GatewayResponseDTO;
import com.digital.servicei.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    private final PaymentService paymentService;

    public GatewayController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, String>> createOrder(
            @RequestParam Long invoiceId,
            @RequestParam Double amount) {

        Map<String, String> response = new HashMap<>();
        String orderId = "order_" + UUID.randomUUID();

        response.put("orderId", orderId);
        response.put("amount", String.valueOf(amount));
        //response.put("upiLink", "upi://pay?orderId=" + orderId);
        response.put("upiLink", "upi://pay?pa=digitalclassroom@upi&pn=Digital Classroom&am=" + amount + "&cu=INR");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/callback")
    public ResponseEntity<GatewayResponseDTO> paymentCallback(
            @Valid @RequestBody GatewayResponseDTO response) {
        GatewayResponseDTO processed = paymentService.processGatewayCallback(response);
        return ResponseEntity.ok(processed);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-order/{orderId}")
    public ResponseEntity<Map<String, String>> updateOrder(
            @PathVariable String orderId,
            @RequestParam(required = false) Double newAmount,
            @RequestParam(required = false) String status) {

        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId);
        if (newAmount != null) {
            response.put("newAmount", String.valueOf(newAmount));
        }
        if (status != null) {
            response.put("status", status);
        }
        response.put("message", "Order updated successfully (mock).");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable String orderId) {
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "CANCELLED");
        response.put("message", "Order cancelled successfully (mock).");
        return ResponseEntity.ok(response);
    }
}
