package com.digital.serviceimpl;


import com.digital.dto.FineDTO;
import com.digital.entity.*;
import com.digital.enums.EventType;
import com.digital.enums.FineStatus;
import com.digital.exception.BusinessException;
import com.digital.exception.NotFoundException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.FineService;
import com.digital.servicei.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineTransactionRepository fineRepo;
    private final LibraryMemberRepository memberRepo;
    private final NotificationService notificationService;
    private final BookIssueRepository issueRepo;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;

    private static final double FINE_PER_DAY = 5.0;



    @Override
    public List<FineDTO> listFinesForMember(Long memberId) {
        LibraryMember member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + memberId));
        List<FineTransaction> fines = fineRepo.findByMemberAndFineStatus(member, FineStatus.UNPAID);
        return fines.stream().map(this::mapToDTO).collect(Collectors.toList());
    }



    @Override
    @Transactional
    public FineDTO payFine(Long fineId, Long paymentId) {
        // Validate payment ID
        if (paymentId == null || paymentId <= 0) {
            throw new BusinessException("Invalid payment ID provided for fine id: " + fineId);
        }
        // Fetch the fine transaction
        FineTransaction fine = fineRepo.findById(fineId)
                .orElseThrow(() -> new NotFoundException("Fine transaction not found with id: " + fineId));
        Student student = studentRepository.findByUser_UserId(fine.getMember().getUserId()).orElseThrow(() ->
                new ResourceNotFoundException("student with userid" + fine.getMember().getUserId() + " not found in database"));
        List<Payment> paymentsByStudent = paymentRepository.findByStudent(student);
        List<Payment> list = paymentsByStudent.stream().filter(payment -> payment.getPaymentId().equals(paymentId)).toList();
        if (!list.isEmpty()) {

            // Check if already paid
            if (fine.getFineStatus() == FineStatus.PAID) {
                throw new BusinessException("Fine is already paid for id: " + fineId);
            }
            // Update fine details
            fine.setFineStatus(FineStatus.PAID);
            fine.setPaidDate(LocalDate.now());
            fine.setPaymentId(paymentId);

            // Save the updated record
            FineTransaction updatedFine = fineRepo.save(fine);

            // Notify the member
            notificationService.sendNotification(
                    updatedFine.getMember().getUserId(),
                    EventType.FINE_PAYMENT,
                    "Fine of ₹" + updatedFine.getFineAmount() + " has been paid successfully on "
                            + updatedFine.getPaidDate() + ". Payment ID: " + updatedFine.getPaymentId()
            );

            // Return DTO
            return mapToDTO(updatedFine);
        }else {
            throw new ResourceNotFoundException ("payment record in payment id "+paymentId+"not found student payment list ");
        }

    }


    @Override
    @Transactional
    public FineDTO createFine(Long issueId, String reason, Double overrideAmount) {
        BookIssue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found with id: " + issueId));

        Optional<FineTransaction> existing = fineRepo.findByIssue(issue);
        if (existing.isPresent()) {
            throw new BusinessException("Fine already exists for issue id: " + issueId);
        }

        LocalDate today = LocalDate.now();
        issue.setReturnDate(today);
        issueRepo.save(issue); // ✅ Save return date

        double fineAmount;
        if (overrideAmount != null) {
            fineAmount = overrideAmount;
        } else {
            LocalDate due = issue.getDueDate();
            long daysLate = ChronoUnit.DAYS.between(due, today);
            fineAmount = today.isAfter(due) ? daysLate * FINE_PER_DAY : 0.0;
        }

        boolean autoPay = isAdminOrLibrarian();

        FineTransaction fine = FineTransaction.builder()
                .issue(issue)
                .member(issue.getMember())
                .fineAmount(fineAmount)
                .fineReason(reason != null ? reason : "Manual fine")
                .fineStatus(autoPay ? FineStatus.PAID : FineStatus.UNPAID)
                .paidDate(autoPay ? LocalDate.now() : null)
                .build();

        fine = fineRepo.save(fine);

        notificationService.sendNotification(
                fine.getMember().getUserId(),
                EventType.FINE_PAYMENT,
                autoPay
                        ? "Fine of ₹" + fine.getFineAmount() + " has been auto-paid by librarian."
                        : "New fine generated: ₹" + fine.getFineAmount());

        return mapToDTO(fine);
    }

    private boolean isAdminOrLibrarian() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_LIBRARIAN") || role.equals("ROLE_LIBRARIAN"));
    }




    private FineDTO mapToDTO (FineTransaction fine){
        return FineDTO.builder()
                .fineId(fine.getFineId())
                .issueId(fine.getIssue().getIssueId())
                .memberId(fine.getMember().getMemberId())
                .fineAmount(fine.getFineAmount())
                .fineReason(fine.getFineReason())
                .fineStatus(fine.getFineStatus().name())
                .paidDate(fine.getPaidDate())
                .paymentId(fine.getPaymentId())
                .build();
    }
//}
//
//    private FineDTO mapToDTO(FineTransaction fine) {
//        return FineDTO.builder()
//                .fineId(fine.getFineId())
//                .issueId(fine.getIssue().getIssueId())
//                .memberId(fine.getMember().getMemberId())
//                .fineAmount(fine.getFineAmount())
//                .fineReason(fine.getFineReason())
//                .fineStatus(fine.getFineStatus().name())
//                .paidDate(fine.getPaidDate())
//                .build();
//    }
}
