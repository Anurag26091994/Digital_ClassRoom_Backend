package com.digital.servicei;


import com.digital.dto.FineDTO;

import java.util.List;

public interface FineService {
    List<FineDTO> listFinesForMember(Long memberId);
    FineDTO payFine(Long fineId, Long paymentId);
    FineDTO createFine(Long issueId, String reason, Double overrideAmount);

}
