package com.carbontrack.analytics.dto;

import com.carbontrack.analytics.AnalyticsPeriod;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SummaryResponse(
        AnalyticsPeriod period,
        LocalDate from,
        LocalDate to,
        BigDecimal totalCo2eKg,
        List<CategoryTotal> byCategory) implements Serializable {
}
