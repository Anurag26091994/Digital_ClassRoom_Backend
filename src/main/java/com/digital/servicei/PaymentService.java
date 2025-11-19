package com.digital.servicei;

import com.digital.dto.GatewayResponseDTO;
import com.digital.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO makePayment(PaymentDTO dto);
    List<PaymentDTO> getPaymentsByStudent();
    GatewayResponseDTO processGatewayCallback(GatewayResponseDTO response);

    PaymentDTO updatePayment(Long paymentId, PaymentDTO dto);

    void deletePayment(Long paymentId);

}
