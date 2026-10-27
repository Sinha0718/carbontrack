package com.carbontrack.benchmark;

import com.carbontrack.benchmark.dto.BenchmarkResponse;
import com.carbontrack.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/benchmark")
@RequiredArgsConstructor
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    @GetMapping("/my-percentile")
    public BenchmarkResponse myPercentile(@AuthenticationPrincipal UserPrincipal principal) {
        return benchmarkService.myPercentile(principal.getId());
    }
}
