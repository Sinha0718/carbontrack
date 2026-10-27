package com.carbontrack.goal.dto;

import com.carbontrack.goal.Goal;
import com.carbontrack.goal.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalResponse(
        Long id,
        BigDecimal targetReductionPct,
        Integer periodDays,
        LocalDate startDate,
        LocalDate endDate,
        GoalStatus status) {

    public static GoalResponse from(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTargetReductionPct(),
                goal.getPeriodDays(),
                goal.getStartDate(),
                goal.getStartDate().plusDays(goal.getPeriodDays()),
                goal.getStatus());
    }
}
