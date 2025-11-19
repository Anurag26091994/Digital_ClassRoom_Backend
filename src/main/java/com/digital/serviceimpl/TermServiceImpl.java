package com.digital.serviceimpl;

import com.digital.servicei.TermService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class TermServiceImpl implements TermService {

    @Override
    public Map<String, LocalDate> getTermDateRange(Long examId, String term) {
        // Example logic (replace with DB-based logic)
        Map<String, LocalDate> range = new HashMap<>();

        if ("Mid Term".equalsIgnoreCase(term)) {
            range.put("startDate", LocalDate.of(2025, 6, 1));
            range.put("endDate", LocalDate.of(2025, 9, 30));
        } else if ("Final Term".equalsIgnoreCase(term)) {
            range.put("startDate", LocalDate.of(2025, 10, 1));
            range.put("endDate", LocalDate.of(2025, 12, 31));
        } else {
            // Default fallback
            range.put("startDate", LocalDate.now().minusMonths(3));
            range.put("endDate", LocalDate.now());
        }

        return range;
    }
}
