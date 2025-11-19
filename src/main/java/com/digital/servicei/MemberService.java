package com.digital.servicei;

import com.digital.dto.MemberCreateRequest;
import com.digital.dto.MemberDTO;

import java.util.List;

public interface MemberService {


    MemberDTO createMember(MemberCreateRequest req);


    MemberDTO getMemberById(Long memberId);


    MemberDTO getMemberByUserId(Long userId);


    List<MemberDTO> listAllMembers();


    MemberDTO updateMember(Long memberId, MemberCreateRequest req);



    void deleteMember(Long memberId);
}
