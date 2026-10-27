package com.carbontrack.goal;

import com.carbontrack.activity.ActivityLogRepository;
import com.carbontrack.common.ResourceNotFoundException;
import com.carbontrack.goal.dto.CreateGoalRequest;
import com.carbontrack.goal.dto.GoalProgressResponse;
import com.carbontrack.goal.dto.GoalResponse;
import com.carbontrack.user.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    @Transactional
    public GoalResponse create(Long userId, CreateGoalRequest request) {
        Goal goal = new Goal();
        goal.setUser(userRepository.getReferenceById(userId));
        goal.setTargetReductionPct(request.targetReductionPct());
        goal.setPeriodDays(request.periodDays());
        goal.setStartDate(request.startDate() != null ? request.startDate() : LocalDate.now(clock));
        goal.setStatus(GoalStatus.ACTIVE);
        return GoalResponse.from(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> list(Long userId) {
        return goalRepository.findByUserId(userId).stream().map(GoalResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GoalProgressResponse progress(Long userId, Long goalId) {
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal " + goalId + " not found"));
        LocalDate start = goal.getStartDate();
        LocalDate end = start.plusDays(goal.getPeriodDays());
        LocalDate today = LocalDate.now(clock);
        LocalDate currentTo = today.isBefore(end) ? today : end;

        BigDecimal baseline = totalOrZero(activityLogRepository.totalCo2e(
                userId, start.minusDays(goal.getPeriodDays()), start.minusDays(1)));
        BigDecimal current = totalOrZero(activityLogRepository.totalCo2e(userId, start, currentTo));

        BigDecimal achievedPct = BigDecimal.ZERO;
        if (baseline.signum() > 0) {
            achievedPct = baseline.subtract(current)
                    .divide(baseline, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        BigDecimal progressPct = achievedPct
                .divide(goal.getTargetReductionPct(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        if (progressPct.compareTo(BigDecimal.valueOf(100)) > 0) {
            progressPct = BigDecimal.valueOf(100);
        }
        if (progressPct.signum() < 0) {
            progressPct = BigDecimal.ZERO;
        }

        long daysElapsed = Math.max(1, ChronoUnit.DAYS.between(start, currentTo) + 1);
        BigDecimal expectedPct = goal.getTargetReductionPct()
                .multiply(BigDecimal.valueOf(daysElapsed))
                .divide(BigDecimal.valueOf(goal.getPeriodDays()), 2, RoundingMode.HALF_UP);

        String projection;
        String alert;
        if (achievedPct.compareTo(goal.getTargetReductionPct()) >= 0) {
            projection = "ACHIEVED";
            alert = "Great job! You have achieved your reduction goal.";
        } else if (achievedPct.compareTo(expectedPct) >= 0) {
            projection = "ON_TRACK";
            alert = "You are on track. Keep it up!";
        } else {
            projection = "BEHIND";
            alert = "You are behind your goal. Try reducing your highest-emission activity this week.";
        }
        return new GoalProgressResponse(goal.getId(), baseline, current,
                goal.getTargetReductionPct(), achievedPct.setScale(2, RoundingMode.HALF_UP),
                progressPct.setScale(2, RoundingMode.HALF_UP), projection, alert);
    }

    private BigDecimal totalOrZero(BigDecimal total) {
        return total == null ? BigDecimal.ZERO : total;
    }
}
