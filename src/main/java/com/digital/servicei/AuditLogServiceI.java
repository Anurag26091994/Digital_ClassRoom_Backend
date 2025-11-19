package com.digital.servicei;

import com.digital.entity.AuditLog;
import com.digital.enums.Action;
import com.digital.enums.Module;

import java.util.List;

public interface AuditLogServiceI {

    void logInfo(Long userId, String username, Action action, Module module);

    List<AuditLog> getAuditLogsByUserId(Long userId);

    List<AuditLog> getAuditLogsByUsername(String username);
}
