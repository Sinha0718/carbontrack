package com.carbontrack.recommendation.dto;

import com.carbontrack.activity.ActivityCategory;
import java.math.BigDecimal;

public record RecommendationResponse(ActivityCategory category, String activityType, BigDecimal totalCo2eKg, String tip) {
}
