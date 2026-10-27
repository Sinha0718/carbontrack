package com.carbontrack.badge.dto;

import com.carbontrack.badge.UserBadge;
import java.time.Instant;

public record UserBadgeResponse(Long badgeId, String name, String description, Instant awardedAt) {
    public static UserBadgeResponse from(UserBadge userBadge) {
        return new UserBadgeResponse(userBadge.getBadge().getId(), userBadge.getBadge().getName(), userBadge.getBadge().getDescription(), userBadge.getAwardedAt());
    }
}
