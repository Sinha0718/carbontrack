package com.carbontrack.badge;

import com.carbontrack.activity.ActivityLogRepository;
import com.carbontrack.goal.Goal;
import com.carbontrack.goal.GoalRepository;
import com.carbontrack.goal.GoalService;
import com.carbontrack.goal.GoalStatus;
import com.carbontrack.user.UserRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BadgeListener {

    private final ActivityLogRepository activityLogRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final GoalRepository goalRepository;
    private final GoalService goalService;
    private final UserRepository userRepository;
    private final Clock clock;

    @EventListener
    public void onActivityLogged(ActivityLoggedEvent event) {
        Long userId = event.userId();
        checkStreak(userId);
        checkReduction(userId);
        checkGoals(userId);
    }

    private void award(Long userId, BadgeTriggerType type, BigDecimal threshold) {
        badgeRepository.findByTriggerTypeAndThreshold(type, threshold).ifPresent(badge -> {
            if (!userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(userRepository.getReferenceById(userId));
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
            }
        });
    }

    private void checkStreak(Long userId) {
        List<LocalDate> dates = activityLogRepository.distinctLogDates(userId);
        if (dates.isEmpty()) {
            return;
        }
        Set<LocalDate> set = new HashSet<>(dates);
        LocalDate expected = dates.get(0);
        int streak = 0;
        while (set.contains(expected)) {
            streak++;
            expected = expected.minusDays(1);
        }
        if (streak >= 7) {
            award(userId, BadgeTriggerType.STREAK, BigDecimal.valueOf(7));
        }
    }

    private void checkReduction(Long userId) {
        LocalDate to = LocalDate.now(clock);
        BigDecimal current = totalOrZero(activityLogRepository.totalCo2e(userId, to.minusDays(29), to));
        BigDecimal previous = totalOrZero(activityLogRepository.totalCo2e(userId, to.minusDays(59), to.minusDays(30)));
        BigDecimal reduction = previous.subtract(current);
        for (int threshold : new int[]{10, 25, 50}) {
            if (reduction.compareTo(BigDecimal.valueOf(threshold)) >= 0) {
                award(userId, BadgeTriggerType.REDUCTION, BigDecimal.valueOf(threshold));
            }
        }
    }

    private void checkGoals(Long userId) {
        for (Goal goal : goalRepository.findByUserIdAndStatus(userId, GoalStatus.ACTIVE)) {
            if ("ACHIEVED".equals(goalService.progress(userId, goal.getId()).projection())) {
                award(userId, BadgeTriggerType.GOAL, BigDecimal.ONE);
                goal.setStatus(GoalStatus.ACHIEVED);
                goalRepository.save(goal);
            }
        }
    }

    private BigDecimal totalOrZero(BigDecimal total) {
        return total == null ? BigDecimal.ZERO : total;
    }
}
