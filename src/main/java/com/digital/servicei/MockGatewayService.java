package com.digital.servicei;

import com.digital.dto.GatewayResponseDTO;

public interface MockGatewayService {
    GatewayResponseDTO createOrder(Long invoiceId, Double amount, String currency);
    GatewayResponseDTO simulatePayment(String orderId, boolean success);
}
