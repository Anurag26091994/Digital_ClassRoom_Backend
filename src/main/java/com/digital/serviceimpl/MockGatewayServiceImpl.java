package com.digital.serviceimpl;

import com.digital.dto.GatewayResponseDTO;
import com.digital.servicei.MockGatewayService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MockGatewayServiceImpl implements MockGatewayService {

    private final Map<String, Map<String, Object>> orders = new HashMap<>();

    @Override
    public GatewayResponseDTO createOrder(Long invoiceId, Double amount, String currency) {
        String orderId = "ORDER-" + UUID.randomUUID();
        String upiOrQr = "upi://pay?pa=mock@upi&tn=FeePayment&am=" + amount;

        Map<String, Object> meta = new HashMap<>();
        meta.put("invoiceId", invoiceId);
        meta.put("amount", amount);
        meta.put("currency", currency);

        orders.put(orderId, meta);

        GatewayResponseDTO dto = new GatewayResponseDTO();
        dto.setOrderId(orderId);
        dto.setStatus("CREATED");
        dto.setMessage("Mock order created");
        dto.setUpiOrQr(upiOrQr);

        return dto;
    }

    @Override
    public GatewayResponseDTO simulatePayment(String orderId, boolean success) {
        GatewayResponseDTO r = new GatewayResponseDTO();

        if (!orders.containsKey(orderId)) {
            r.setStatus("FAILED");
            r.setMessage("Order not found");
            return r;
        }

        String gatewayPaymentId = "GWPAY-" + UUID.randomUUID().toString().substring(0, 12);
        r.setOrderId(orderId);
        r.setGatewayPaymentId(gatewayPaymentId);
        r.setStatus(success ? "SUCCESS" : "FAILED");
        r.setMessage(success ? "Payment successful" : "Payment failed");

        return r;
    }
}
