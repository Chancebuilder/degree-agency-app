package com.degreedean.degreeplanning;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DegreeGoalRepository extends JpaRepository<DegreeGoal, UUID> {
    List<DegreeGoal> findByUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserId(UUID userId);
}

interface DegreePlanRepository extends JpaRepository<DegreePlan, UUID> {
    List<DegreePlan> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

interface PlanMatchRepository extends JpaRepository<PlanMatch, UUID> {
    List<PlanMatch> findByPlanId(UUID planId);
    void deleteByPlanId(UUID planId);
}

interface PlanScenarioRepository extends JpaRepository<PlanScenario, UUID> {
    List<PlanScenario> findByPlanIdOrderByCreatedAtDesc(UUID planId);
    void deleteByPlanId(UUID planId);
}

interface PlanCourseRepository extends JpaRepository<PlanCourse, UUID> {
    List<PlanCourse> findByScenarioIdOrderByStartWeekAsc(UUID scenarioId);
}

interface CostItemRepository extends JpaRepository<CostItem, UUID> {
    List<CostItem> findByScenarioId(UUID scenarioId);
}

interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByPlanIdOrderByRankAsc(UUID planId);
    void deleteByPlanId(UUID planId);
}
