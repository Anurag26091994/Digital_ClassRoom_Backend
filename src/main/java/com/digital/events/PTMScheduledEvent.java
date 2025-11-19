package com.digital.events;

import com.digital.entity.PTM;

import java.util.List;

public record PTMScheduledEvent(PTM ptm, List<Long> studentIds) {
}
