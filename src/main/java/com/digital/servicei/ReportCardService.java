package com.digital.servicei;

import com.digital.dto.ReportCardResponse;
import com.digital.entity.ReportCard;

import java.time.LocalDate;
import java.util.List;

public interface ReportCardService {
    //byte[] generateReportCardPdf(Long studentId, String term, Long requesterId);
    List<ReportCard> generateReportCards(Long classId, Long sectionId, Long subjectId, String term, List<Long> examIds);
    LocalDate[] getTermDateRange(Long classId, String term);
}

