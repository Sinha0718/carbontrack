package com.carbontrack.goal.dto;

import java.math.BigDecimal;

public record GoalProgressResponse(
        Long goalId,
        BigDecimal baselineCo2eKg,
        BigDecimal currentCo2eKg,
        BigDecimal targetReductionPct,
        BigDecimal achievedReductionPct,
        BigDecimal progressPct,
        String projection,
        String alert) {
}
