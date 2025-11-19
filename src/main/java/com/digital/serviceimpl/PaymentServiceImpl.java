package com.digital.serviceimpl;

import com.digital.dto.GatewayResponseDTO;

import com.digital.dto.PaymentDTO;

import com.digital.entity.Invoice;

import com.digital.entity.Payment;

import com.digital.entity.Student;

import com.digital.enums.PaymentMode;

import com.digital.enums.PaymentStatus;

import com.digital.repository.InvoiceRepository;

import com.digital.repository.PaymentRepository;

import com.digital.repository.StudentRepository;

import com.digital.servicei.PaymentService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.List;

import java.util.stream.Collectors;

@Service

@Transactional

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;

    private final InvoiceRepository invoiceRepo;

    private final StudentRepository studentRepo;

    public PaymentServiceImpl(PaymentRepository paymentRepo,

                              InvoiceRepository invoiceRepo,

                              StudentRepository studentRepo) {

        this.paymentRepo = paymentRepo;

        this.invoiceRepo = invoiceRepo;

        this.studentRepo = studentRepo;

    }

    @Transactional

    @Override
    public PaymentDTO makePayment(PaymentDTO dto) {
        Invoice inv = invoiceRepo.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepo.findByUserUsername(username).orElseThrow(() -> new RuntimeException("Student not found"));
//        Student student = studentRepo.findById(dto.getStudentId())
//                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Ensure invoice belongs to this student
        if (!inv.getStudent().getStudentRegId().equals(student.getStudentId())) {
            throw new RuntimeException("Invoice does not belong to this student");
        }

        BigDecimal remaining = inv.getTotalDue().subtract(inv.getAmountPaid());
        if (dto.getAmountPaid().compareTo(remaining) > 0) {
            throw new RuntimeException("Payment exceeds remaining due amount");
        }

        Payment payment = new Payment();
        payment.setInvoice(inv);
        payment.setStudent(student);
        payment.setAmountPaid(dto.getAmountPaid());
        payment.setPaymentMode(PaymentMode.valueOf(dto.getPaymentMode()));

        // ✅ Generate unique transactionId if null
        if (dto.getTransactionId() == null || dto.getTransactionId().isEmpty()) {
            payment.setTransactionId("TXN-" + System.currentTimeMillis() + "-" + student.getStudentRegId());
        } else {
            payment.setTransactionId(dto.getTransactionId());
        }

        payment.setGatewayReferenceId(dto.getGatewayReferenceId());
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepo.save(payment);

        // If not gateway payment, mark success immediately
        if (!saved.getPaymentMode().equals(PaymentMode.PAYMENT_GATEWAY)) {
            saved.setStatus(PaymentStatus.SUCCESS);
            inv.setAmountPaid(inv.getAmountPaid().add(saved.getAmountPaid()));
            inv.recomputeStatus();
            invoiceRepo.save(inv);
        }

        return toDTO(saved);
    }

    @Override

    public List<PaymentDTO> getPaymentsByStudent() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepo.findByUserUsername(username).orElseThrow(() -> new RuntimeException("Student not found"));
        List<Payment> payments;

        if (student.getStudentId() != null) {

//            Student student = studentRepo.findById(studentId)
//
//                    .orElseThrow(() -> new RuntimeException("Student not found"));

            payments = paymentRepo.findByStudent(student);

        } else {

            payments = paymentRepo.findAll();

        }

        return payments.stream().map(this::toDTO).collect(Collectors.toList());

    }

    @Override

    public GatewayResponseDTO processGatewayCallback(GatewayResponseDTO response) {

        Payment payment = paymentRepo.findByTransactionId(response.getTransactionId())

                .orElseThrow(() -> new RuntimeException("Payment not found for txn: " + response.getTransactionId()));

        if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {

            payment.setStatus(PaymentStatus.SUCCESS);

            Invoice inv = payment.getInvoice();

            inv.setAmountPaid(inv.getAmountPaid().add(payment.getAmountPaid()));

            inv.recomputeStatus();

            invoiceRepo.save(inv);

        } else {

            payment.setStatus(PaymentStatus.FAILED);

        }

        paymentRepo.save(payment);

        return response;

    }

    @Override

    public PaymentDTO updatePayment(Long paymentId, PaymentDTO dto) {

        Payment payment = paymentRepo.findById(paymentId)

                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (dto.getAmountPaid() != null) payment.setAmountPaid(dto.getAmountPaid());

        if (dto.getPaymentMode() != null) payment.setPaymentMode(PaymentMode.valueOf(dto.getPaymentMode()));

        if (dto.getStatus() != null) payment.setStatus(PaymentStatus.valueOf(dto.getStatus()));

        Payment updated = paymentRepo.save(payment);

        return toDTO(updated);

    }

    @Override

    public void deletePayment(Long paymentId) {

        Payment payment = paymentRepo.findById(paymentId)

                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        Invoice inv = payment.getInvoice();

        if (payment.getStatus() == PaymentStatus.SUCCESS) {

            inv.setAmountPaid(inv.getAmountPaid().subtract(payment.getAmountPaid()));

            inv.recomputeStatus();

            invoiceRepo.save(inv);

        }

        paymentRepo.delete(payment);

    }

    private PaymentDTO toDTO(Payment payment) {

        PaymentDTO dto = new PaymentDTO();

        dto.setPaymentId(payment.getPaymentId());

        dto.setInvoiceId(payment.getInvoice().getInvoiceId());

        dto.setStudentId(payment.getStudent().getStudentRegId());

        dto.setAmountPaid(payment.getAmountPaid());

        dto.setPaymentMode(payment.getPaymentMode().name());

        dto.setTransactionId(payment.getTransactionId());

        dto.setGatewayReferenceId(payment.getGatewayReferenceId());

        dto.setPaymentDate(payment.getPaymentDate().toString());

        dto.setPaymentMethod(payment.getPaymentMode().name());
        dto.setStatus(payment.getStatus().name());

        return dto;

    }

}