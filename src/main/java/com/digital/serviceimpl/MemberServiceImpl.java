package com.digital.serviceimpl;

import com.digital.dto.MemberCreateRequest;
import com.digital.dto.MemberDTO;
import com.digital.entity.LibraryMember;
import com.digital.enums.MemberStatus;
import com.digital.repository.LibraryMemberRepository;
import com.digital.servicei.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final LibraryMemberRepository memberRepository;

    @Override
    public MemberDTO createMember(MemberCreateRequest req) {
        if (memberRepository.findByUserId(req.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Member already exists for userId: " + req.getUserId());
        }

        LibraryMember member = LibraryMember.builder()
                .userId(req.getUserId())
                .membershipType(req.getMembershipType())
                .joinDate(LocalDate.now())
                .status(MemberStatus.ACTIVE)
                .totalIssuedBooks(0)
                .build();

        LibraryMember saved = memberRepository.save(member);
        return toDTO(saved);
    }

    @Override
    public MemberDTO getMemberById(Long memberId) {
        LibraryMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return toDTO(member);
    }

    @Override
    public MemberDTO getMemberByUserId(Long userId) {
        LibraryMember member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found for userId: " + userId));
        return toDTO(member);
    }

    @Override
    public List<MemberDTO> listAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO updateMember(Long memberId, MemberCreateRequest req) {
        LibraryMember existing = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        existing.setMembershipType(req.getMembershipType());

        LibraryMember updated = memberRepository.save(existing);
        return toDTO(updated);
    }

    @Override
    public void deleteMember(Long memberId) {
        LibraryMember existing = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // You might prefer soft delete — mark status = INACTIVE
        existing.setStatus(MemberStatus.INACTIVE);
        memberRepository.save(existing);
    }

    // Utility mapping method
    private MemberDTO toDTO(LibraryMember m) {
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(m.getMemberId());
        dto.setUserId(m.getUserId());
        dto.setMembershipType(m.getMembershipType());
        dto.setJoinDate(m.getJoinDate());
        dto.setStatus(m.getStatus());
        dto.setTotalIssuedBooks(m.getTotalIssuedBooks());
        return dto;
    }
}
