package com.carbontrack.activity;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.carbontrack.analytics.dto.CategoryTotal;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.carbontrack.benchmark.dto.UserTotal;
import com.carbontrack.recommendation.dto.TopActivity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @EntityGraph(attributePaths = "emissionFactor")
    Page<ActivityLog> findByUserIdAndLogDateBetween(Long userId, LocalDate from, LocalDate to, Pageable pageable);

    @EntityGraph(attributePaths = "emissionFactor")
    Page<ActivityLog> findByUserIdAndCategoryAndLogDateBetween(
            Long userId, ActivityCategory category, LocalDate from, LocalDate to, Pageable pageable);

    @EntityGraph(attributePaths = "emissionFactor")
    Optional<ActivityLog> findByIdAndUserId(Long id, Long userId);
    @Query("SELECT SUM(a.co2eKg) FROM ActivityLog a WHERE a.user.id = :userId AND a.logDate BETWEEN :from AND :to")
    BigDecimal totalCo2e(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT new com.carbontrack.analytics.dto.CategoryTotal(a.category, SUM(a.co2eKg)) FROM ActivityLog a WHERE a.user.id = :userId AND a.logDate BETWEEN :from AND :to GROUP BY a.category ORDER BY SUM(a.co2eKg) DESC")
    List<CategoryTotal> totalsByCategory(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT new com.carbontrack.recommendation.dto.TopActivity(a.activityType, a.category, SUM(a.co2eKg)) FROM ActivityLog a WHERE a.user.id = :userId AND a.logDate BETWEEN :from AND :to GROUP BY a.activityType, a.category ORDER BY SUM(a.co2eKg) DESC")
    List<TopActivity> totalsByActivityType(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT DISTINCT a.logDate FROM ActivityLog a WHERE a.user.id = :userId ORDER BY a.logDate DESC")
    List<LocalDate> distinctLogDates(@Param("userId") Long userId);

    @Query("SELECT new com.carbontrack.benchmark.dto.UserTotal(a.user.id, SUM(a.co2eKg)) FROM ActivityLog a WHERE a.logDate BETWEEN :from AND :to GROUP BY a.user.id")
    List<UserTotal> totalsByUser(@Param("from") LocalDate from, @Param("to") LocalDate to);

}
