package com.carbontrack.analytics;

import com.carbontrack.analytics.dto.CategoryTotal;
import com.carbontrack.analytics.dto.SummaryResponse;
import com.carbontrack.security.UserPrincipal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public SummaryResponse summary(@AuthenticationPrincipal UserPrincipal principal,
                                   @RequestParam(defaultValue = "WEEKLY") AnalyticsPeriod period) {
        return analyticsService.summary(principal.getId(), period);
    }

    @GetMapping("/by-category")
    public List<CategoryTotal> byCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return analyticsService.byCategory(principal.getId(), from, to);
    }
}
