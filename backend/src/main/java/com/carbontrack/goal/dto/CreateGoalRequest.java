package com.carbontrack.goal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequest(
        @NotNull @DecimalMin("0.01") @DecimalMax("100") BigDecimal targetReductionPct,
        @NotNull @Min(7) @Max(365) Integer periodDays,
        LocalDate startDate) {
}
