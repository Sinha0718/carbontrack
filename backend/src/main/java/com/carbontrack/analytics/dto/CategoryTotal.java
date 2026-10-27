package com.carbontrack.analytics.dto;

import com.carbontrack.activity.ActivityCategory;
import java.io.Serializable;
import java.math.BigDecimal;

public record CategoryTotal(ActivityCategory category, BigDecimal totalCo2eKg) implements Serializable {
}
