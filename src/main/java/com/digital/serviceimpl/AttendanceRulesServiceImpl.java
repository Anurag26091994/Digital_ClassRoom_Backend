package com.digital.serviceimpl;

import com.digital.entity.AttendanceRule;
import com.digital.enums.AttendanceRuleName;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.AttendanceRulesRepository;
import com.digital.servicei.AttendanceRulesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceRulesServiceImpl implements AttendanceRulesService {

    private final AttendanceRulesRepository attendanceRulesRepository;

    public AttendanceRulesServiceImpl(AttendanceRulesRepository attendanceRulesRepository) {
        this.attendanceRulesRepository = attendanceRulesRepository;
    }

    @Override
    public String setRule(AttendanceRule attendanceRules) {

        if(attendanceRules.getRuleName().equals(AttendanceRuleName.HALF_DAY)){

            if(attendanceRules.getSessionDurationPercentage2()> attendanceRules.getSessionDurationPercentage1()){
                attendanceRulesRepository.save(attendanceRules);
                return "Attendance rule has created.";
            }else{
                return "SessionDurationPercentage2 value must be > SessionDurationPercentage1 value";
            }
        }

        attendanceRulesRepository.save(attendanceRules);
        return "Attendance rule has created.";
    }

    @Override
    public AttendanceRule getAttendanceRule(AttendanceRuleName ruleName) {
        return attendanceRulesRepository.findByRuleName(ruleName).orElseThrow(() -> new ResourceNotFoundException("No rule found in database with given rule name " + ruleName));
    }

    @Override
    public List<AttendanceRule> getAllAttendanceRule() {
        return attendanceRulesRepository.findAll();
    }

    @Override
    public String updateRule(Long attendanceRuleId, AttendanceRule attendanceRule) {

        if(attendanceRulesRepository.findById(attendanceRuleId).isPresent()){

            if(attendanceRule.getRuleName().equals(AttendanceRuleName.HALF_DAY)){

                if(attendanceRule.getSessionDurationPercentage2()> attendanceRule.getSessionDurationPercentage1()){
                    attendanceRulesRepository.save(attendanceRule);
                    return "Attendance rule has created.";
                }else{
                    return "SessionDurationPercentage2 value must be > SessionDurationPercentage1 value";
                }
            }
            attendanceRulesRepository.save(attendanceRule);
            return "Attendance rule updated successfully.";
        }
        else{
            throw  new ResourceNotFoundException("No rule found in database with given ID: " + attendanceRuleId);
        }
    }

    @Override
    public String deleteRule(Long attendanceRuleId) {

        AttendanceRule attendanceRules = attendanceRulesRepository.findById(attendanceRuleId).orElseThrow(() -> new ResourceNotFoundException("No rule found in database with given ID: " + attendanceRuleId));

        attendanceRulesRepository.delete(attendanceRules);

        return "Attendance rule deleted successfully.";
    }
}
