package com.carbontrack.analytics;

import com.carbontrack.activity.ActivityLogRepository;
import com.carbontrack.analytics.dto.CategoryTotal;
import com.carbontrack.analytics.dto.SummaryResponse;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;

    @Cacheable(value = "analyticsSummary", key = "#userId + '-' + #period")
    @Transactional(readOnly = true)
    public SummaryResponse summary(Long userId, AnalyticsPeriod period) {
        LocalDate to = LocalDate.now(clock);
        LocalDate from = switch (period) {
            case DAILY -> to;
            case WEEKLY -> to.with(DayOfWeek.MONDAY);
            case MONTHLY -> to.withDayOfMonth(1);
        };
        BigDecimal total = activityLogRepository.totalCo2e(userId, from, to);
        List<CategoryTotal> byCategory = activityLogRepository.totalsByCategory(userId, from, to);
        return new SummaryResponse(period, from, to, total == null ? BigDecimal.ZERO : total, byCategory);
    }

    @Cacheable(value = "analyticsByCategory", key = "#userId + '-' + #from + '-' + #to")
    @Transactional(readOnly = true)
    public List<CategoryTotal> byCategory(Long userId, LocalDate from, LocalDate to) {
        return activityLogRepository.totalsByCategory(userId, from, to);
    }
}
