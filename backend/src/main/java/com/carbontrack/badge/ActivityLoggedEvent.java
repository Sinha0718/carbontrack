package com.carbontrack.badge;

import java.time.LocalDate;

public record ActivityLoggedEvent(Long userId, LocalDate logDate) {
}
