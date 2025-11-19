package com.digital.repository;

import com.digital.entity.Invoice;
import com.digital.entity.Payment;
import com.digital.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoice(Invoice invoice);

    List<Payment> findByInvoice_InvoiceId(Long invoiceId);

    List<Payment> findByStudent(Student student);

    List<Payment> findByStudent_StudentRegId(Long studentRegId);

    Optional<Payment> findByTransactionId(String transactionId);

}
