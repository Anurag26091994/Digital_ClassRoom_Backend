package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private StudentResponse profile;
    private String attendanceSummary;
    private List<String> pendingAssignments;
    private String resultOverview;
    private List<String> recentNotifications;
}
