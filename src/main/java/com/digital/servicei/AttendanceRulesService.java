package com.digital.servicei;

import com.digital.entity.AttendanceRule;
import com.digital.enums.AttendanceRuleName;

import java.util.List;

public interface AttendanceRulesService {

    String setRule(AttendanceRule attendanceRules);

    AttendanceRule getAttendanceRule(AttendanceRuleName ruleName);

    List<AttendanceRule> getAllAttendanceRule();

    String updateRule(Long attendanceRulesId, AttendanceRule attendanceRule);

    String deleteRule(Long attendanceRulesId);
}
