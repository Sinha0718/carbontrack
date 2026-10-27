package com.carbontrack.recommendation.dto;

import com.carbontrack.activity.ActivityCategory;
import java.math.BigDecimal;

public record TopActivity(String activityType, ActivityCategory category, BigDecimal totalCo2eKg) {
}
