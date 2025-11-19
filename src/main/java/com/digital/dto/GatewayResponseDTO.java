package com.digital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GatewayResponseDTO {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Gateway Payment ID is required")
    private String gatewayPaymentId;

    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must be at most 20 characters")
    private String status;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
    @Size(max = 200, message = "UPI/QR link cannot exceed 200 characters")
    private String upiOrQr;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUpiOrQr() { return upiOrQr; }
    public void setUpiOrQr(String upiOrQr) { this.upiOrQr = upiOrQr; }
}
