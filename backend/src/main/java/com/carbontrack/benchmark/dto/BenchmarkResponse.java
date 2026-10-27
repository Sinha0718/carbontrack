package com.carbontrack.benchmark.dto;

import java.math.BigDecimal;

public record BenchmarkResponse(BigDecimal myTotalKg, BigDecimal averageKg, BigDecimal percentile, String message) {
}
