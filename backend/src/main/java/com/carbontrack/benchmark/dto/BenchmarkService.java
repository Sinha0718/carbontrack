package com.carbontrack.benchmark;

import com.carbontrack.activity.ActivityLogRepository;
import com.carbontrack.benchmark.dto.BenchmarkResponse;
import com.carbontrack.benchmark.dto.UserTotal;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public BenchmarkResponse myPercentile(Long userId) {
        LocalDate to = LocalDate.now(clock);
        LocalDate from = to.minusDays(29);
        List<UserTotal> all = activityLogRepository.totalsByUser(from, to);
        if (all.isEmpty()) {
            return new BenchmarkResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "No activity data yet — log a few activities first.");
        }
        BigDecimal mine = all.stream().filter(u -> u.userId().equals(userId))
                .map(UserTotal::totalCo2eKg).findFirst().orElse(BigDecimal.ZERO);
        BigDecimal sum = all.stream().map(UserTotal::totalCo2eKg).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(BigDecimal.valueOf(all.size()), 2, RoundingMode.HALF_UP);
        long moreThanMe = all.stream().filter(u -> u.totalCo2eKg().compareTo(mine) > 0).count();
        BigDecimal percentile = BigDecimal.valueOf(moreThanMe * 100)
                .divide(BigDecimal.valueOf(all.size()), 2, RoundingMode.HALF_UP);
        String message = percentile.compareTo(BigDecimal.valueOf(50)) >= 0
                ? "You emit less than " + percentile + "% of users in the last 30 days. Keep it up!"
                : "You emit more than most users in the last 30 days — check your recommendations for the biggest saving.";
        return new BenchmarkResponse(mine, average, percentile, message);
    }
}
