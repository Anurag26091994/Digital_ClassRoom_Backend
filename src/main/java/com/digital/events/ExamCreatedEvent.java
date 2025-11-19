package com.digital.events;

import java.util.List;

public record ExamCreatedEvent(
        Long examId,
        List<Long> studentIds,
        List<Long> teacherIds
) {}
