package com.carbontrack.recommendation;

import com.carbontrack.activity.ActivityCategory;
import com.carbontrack.activity.ActivityLogRepository;
import com.carbontrack.recommendation.dto.RecommendationResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<RecommendationResponse> recommendations(Long userId) {
        LocalDate to = LocalDate.now(clock);
        LocalDate from = to.minusDays(29);
        return activityLogRepository.totalsByActivityType(userId, from, to).stream().limit(3)
                .map(t -> new RecommendationResponse(t.category(), t.activityType(), t.totalCo2eKg(), tipFor(t.category())))
                .toList();
    }

    private String tipFor(ActivityCategory category) {
        return switch (category) {
            case TRANSPORT -> "Try bus, metro or carpool for short trips instead of a private car — this is usually the biggest saving.";
            case ELECTRICITY -> "Switch off unused appliances and use LED lighting; check your highest-consumption devices first.";
            case FOOD -> "Cut down high-emission meals a few times a week and prefer local, seasonal food.";
            case SHOPPING -> "Buy less, prefer second-hand where possible, and avoid impulse fast-fashion buys.";
        };
    }
}
