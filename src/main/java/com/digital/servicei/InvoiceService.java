package com.digital.servicei;

import com.digital.dto.InvoiceDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceDTO generateInvoice(Long studentId, Long feeId, String dueDate);
    InvoiceDTO getInvoice(Long invoiceId);
    List<InvoiceDTO> getInvoicesByStudent(String username);
    InvoiceDTO applyAdjustment(Long invoiceId, double adjustmentAmount);

    InvoiceDTO updateInvoice(Long id, Long studentId, String dueDate);

    void deleteInvoice(Long id);

    List<InvoiceDTO> getAllInvoices();

}
