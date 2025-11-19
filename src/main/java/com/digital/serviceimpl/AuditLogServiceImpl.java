package com.digital.serviceimpl;

import com.digital.entity.AuditLog;
import com.digital.enums.Action;
import com.digital.enums.Module;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.AuditLogRepository;
import com.digital.servicei.AuditLogServiceI;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogServiceI {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logInfo(Long userId, String username, Action action, Module module) {
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .module(module)
                .time(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> getAuditLogsByUserId(Long userId) {
        return auditLogRepository.findAllByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("No logs available for the given user id"));
    }

    @Override
    public List<AuditLog> getAuditLogsByUsername(String username) {
        return auditLogRepository.findAllByUsername(username);
    }
}
