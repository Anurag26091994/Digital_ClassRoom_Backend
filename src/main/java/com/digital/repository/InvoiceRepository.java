package com.digital.repository;

import com.digital.entity.FeeStructure;
import com.digital.entity.Invoice;
import com.digital.entity.Student;
import com.digital.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStudent(Student student);

    List<Invoice> findByStudent_StudentRegId(Long studentRegId);
    Optional<Invoice> findByInvoiceId(Long invoiceId);


    List<Invoice> findByStudentAndFeeStructureAndStatus(Student student, FeeStructure feeStructure, InvoiceStatus status);

}
