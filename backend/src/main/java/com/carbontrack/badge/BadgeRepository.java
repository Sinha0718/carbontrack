package com.carbontrack.badge;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
Optional<Badge> findByTriggerTypeAndThreshold(BadgeTriggerType triggerType, BigDecimal threshold);

}
