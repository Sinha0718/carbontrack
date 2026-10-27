package com.carbontrack.recommendation;

import com.carbontrack.recommendation.dto.RecommendationResponse;
import com.carbontrack.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public List<RecommendationResponse> recommendations(@AuthenticationPrincipal UserPrincipal principal) {
        return recommendationService.recommendations(principal.getId());
    }
}
