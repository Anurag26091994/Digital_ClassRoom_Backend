package com.digital.events;

import java.util.List;

public record ReportCardGeneratedEvent(
        Long reportCardId,
        List<Long> studentIds
) {}
