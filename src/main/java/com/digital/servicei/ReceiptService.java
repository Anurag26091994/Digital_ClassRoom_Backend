package com.digital.servicei;

public interface ReceiptService {

    /**
     * Generates PDF receipt for a given invoice
     * @param invoiceId - invoice ID
     * @return PDF as byte array
     * @throws Exception if invoice not found or PDF generation fails
     */
    byte[] generateReceipt(Long invoiceId) throws Exception;
}
