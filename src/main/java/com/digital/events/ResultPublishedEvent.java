package com.digital.events;

import java.util.List;

public record ResultPublishedEvent(
        Long resultId,
        List<Long> studentIds
) {}
