package com.digital.serviceimpl;

import com.digital.dto.InvoiceDTO;
import com.digital.entity.FeeStructure;
import com.digital.entity.Invoice;
import com.digital.entity.Student;
import com.digital.enums.InvoiceStatus;
import com.digital.repository.FeeStructureRepository;
import com.digital.repository.InvoiceRepository;
import com.digital.repository.StudentRepository;
import com.digital.servicei.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final FeeStructureRepository feeRepo;
    private final StudentRepository studentRepo;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepo,
                              FeeStructureRepository feeRepo,
                              StudentRepository studentRepo) {
        this.invoiceRepo = invoiceRepo;
        this.feeRepo = feeRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public InvoiceDTO generateInvoice(Long studentId, Long feeId, String dueDateStr) {
        FeeStructure fs = feeRepo.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setFeeStructure(fs);
        invoice.setTotalDue(fs.getTotalAmount());
        invoice.setDueDate(LocalDate.parse(dueDateStr));
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setAmountPaid(BigDecimal.ZERO);

        return toDTO(invoiceRepo.save(invoice));
    }

    @Override
    public InvoiceDTO getInvoice(Long invoiceId) {
        return invoiceRepo.findById(invoiceId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public List<InvoiceDTO> getInvoicesByStudent(String username) {
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return invoiceRepo.findByStudent(student).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDTO applyAdjustment(Long invoiceId, double adjustmentAmount) {
        Invoice inv = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BigDecimal adj = BigDecimal.valueOf(adjustmentAmount);
        inv.setTotalDue(inv.getTotalDue().add(adj));
        inv.recomputeStatus();

        return toDTO(invoiceRepo.save(inv));
    }

    @Override
    public InvoiceDTO updateInvoice(Long id, Long studentId, String dueDateStr) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (studentId != null) {
            Student student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            invoice.setStudent(student);
        }

        if (dueDateStr != null) {
            invoice.setDueDate(LocalDate.parse(dueDateStr));
        }

        invoice.recomputeStatus();
        return toDTO(invoiceRepo.save(invoice));
    }

    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoiceRepo.delete(invoice);
    }

    // ✅ UPDATED METHOD
    @Override
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Invoice getOrCreateInvoice(Long studentId, Long feeId, String dueDateStr) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        FeeStructure fee = feeRepo.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        List<Invoice> existing = invoiceRepo.findByStudentAndFeeStructureAndStatus(student, fee, InvoiceStatus.UNPAID);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setFeeStructure(fee);
        invoice.setTotalDue(fee.getTotalAmount());
        invoice.setDueDate(LocalDate.parse(dueDateStr));
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setAmountPaid(BigDecimal.ZERO);

        return invoiceRepo.save(invoice);
    }

    private InvoiceDTO toDTO(Invoice inv) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceId(inv.getInvoiceId());
        dto.setStudentId(inv.getStudent().getStudentRegId());
        dto.setFeeId(inv.getFeeStructure().getFeeId());
        dto.setTotalDue(inv.getTotalDue());
        dto.setDueDate(inv.getDueDate());
        dto.setStatus(inv.getStatus());
        dto.setAmountPaid(inv.getAmountPaid());
        return dto;
    }
}
