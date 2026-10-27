package com.carbontrack.badge;

import com.carbontrack.badge.dto.UserBadgeResponse;
import com.carbontrack.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final UserBadgeRepository userBadgeRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public List<UserBadgeResponse> myBadges(@AuthenticationPrincipal UserPrincipal principal) {
        return userBadgeRepository.findByUserId(principal.getId()).stream()
                .map(UserBadgeResponse::from)
                .toList();
    }
}
