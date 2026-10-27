package com.carbontrack.goal;

import com.carbontrack.goal.dto.CreateGoalRequest;
import com.carbontrack.goal.dto.GoalProgressResponse;
import com.carbontrack.goal.dto.GoalResponse;
import com.carbontrack.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@AuthenticationPrincipal UserPrincipal principal,
                               @Valid @RequestBody CreateGoalRequest request) {
        return goalService.create(principal.getId(), request);
    }

    @GetMapping
    public List<GoalResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return goalService.list(principal.getId());
    }

    @GetMapping("/{id}/progress")
    public GoalProgressResponse progress(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return goalService.progress(principal.getId(), id);
    }
}
