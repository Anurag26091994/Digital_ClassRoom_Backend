package com.digital.servicei;

import java.time.LocalDate;
import java.util.Map;

public interface TermService {
    Map<String, LocalDate> getTermDateRange(Long examId, String term);
}
