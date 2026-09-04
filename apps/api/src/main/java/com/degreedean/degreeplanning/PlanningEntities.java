package com.degreedean.degreeplanning;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "degree_goals")
class DegreeGoal {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "degree_family", nullable = false)
    private String degreeFamily;
    @Column(name = "target_institution_id")
    private UUID targetInstitutionId;
    @Column(name = "target_program_id")
    private UUID targetProgramId;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getDegreeFamily() { return degreeFamily; }
    public void setDegreeFamily(String degreeFamily) { this.degreeFamily = degreeFamily; }
    public UUID getTargetInstitutionId() { return targetInstitutionId; }
    public void setTargetInstitutionId(UUID targetInstitutionId) { this.targetInstitutionId = targetInstitutionId; }
    public UUID getTargetProgramId() { return targetProgramId; }
    public void setTargetProgramId(UUID targetProgramId) { this.targetProgramId = targetProgramId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

@Entity
@Table(name = "degree_plans")
class DegreePlan {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "goal_id", nullable = false)
    private UUID goalId;
    @Column(name = "program_id", nullable = false)
    private UUID programId;
    @Column(name = "catalog_year", nullable = false)
    private String catalogYear;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getGoalId() { return goalId; }
    public void setGoalId(UUID goalId) { this.goalId = goalId; }
    public UUID getProgramId() { return programId; }
    public void setProgramId(UUID programId) { this.programId = programId; }
    public String getCatalogYear() { return catalogYear; }
    public void setCatalogYear(String catalogYear) { this.catalogYear = catalogYear; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

@Entity
@Table(name = "plan_matches")
class PlanMatch {
    @Id
    private UUID id;
    @Column(name = "plan_id", nullable = false)
    private UUID planId;
    @Column(name = "asset_id")
    private UUID assetId;
    @Column(name = "equivalency_id")
    private UUID equivalencyId;
    @Column(name = "slot_id")
    private UUID slotId;
    @Column(nullable = false)
    private String classification;
    private String reason;
    @Column(nullable = false)
    private String confidence;
    @Column(name = "policy_rule_ids")
    private String policyRuleIds;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }
    public UUID getEquivalencyId() { return equivalencyId; }
    public void setEquivalencyId(UUID equivalencyId) { this.equivalencyId = equivalencyId; }
    public UUID getSlotId() { return slotId; }
    public void setSlotId(UUID slotId) { this.slotId = slotId; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public String getPolicyRuleIds() { return policyRuleIds; }
    public void setPolicyRuleIds(String policyRuleIds) { this.policyRuleIds = policyRuleIds; }
}

@Entity
@Table(name = "plan_scenarios")
class PlanScenario {
    @Id
    private UUID id;
    @Column(name = "plan_id", nullable = false)
    private UUID planId;
    @Column(name = "scenario_type", nullable = false)
    private String scenarioType;
    @Column(name = "projected_completion_date")
    private LocalDate projectedCompletionDate;
    @Column(name = "total_cost")
    private BigDecimal totalCost;
    @Column(name = "weeks_remaining")
    private Integer weeksRemaining;
    @Column(name = "subscription_months")
    private Integer subscriptionMonths;
    @Column(name = "course_count")
    private Integer courseCount;
    private String feasibility;
    private String confidence;
    @Column(name = "blocked_reason")
    private String blockedReason;
    @Column(name = "stale_blocked", nullable = false)
    private boolean staleBlocked;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public String getScenarioType() { return scenarioType; }
    public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }
    public LocalDate getProjectedCompletionDate() { return projectedCompletionDate; }
    public void setProjectedCompletionDate(LocalDate projectedCompletionDate) { this.projectedCompletionDate = projectedCompletionDate; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public Integer getWeeksRemaining() { return weeksRemaining; }
    public void setWeeksRemaining(Integer weeksRemaining) { this.weeksRemaining = weeksRemaining; }
    public Integer getSubscriptionMonths() { return subscriptionMonths; }
    public void setSubscriptionMonths(Integer subscriptionMonths) { this.subscriptionMonths = subscriptionMonths; }
    public Integer getCourseCount() { return courseCount; }
    public void setCourseCount(Integer courseCount) { this.courseCount = courseCount; }
    public String getFeasibility() { return feasibility; }
    public void setFeasibility(String feasibility) { this.feasibility = feasibility; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public String getBlockedReason() { return blockedReason; }
    public void setBlockedReason(String blockedReason) { this.blockedReason = blockedReason; }
    public boolean isStaleBlocked() { return staleBlocked; }
    public void setStaleBlocked(boolean staleBlocked) { this.staleBlocked = staleBlocked; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

@Entity
@Table(name = "plan_courses")
class PlanCourse {
    @Id
    private UUID id;
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;
    @Column(name = "slot_id")
    private UUID slotId;
    @Column(name = "opportunity_id")
    private UUID opportunityId;
    @Column(name = "start_week")
    private Integer startWeek;
    @Column(name = "end_week")
    private Integer endWeek;
    private BigDecimal cost;
    @Column(name = "provider_code")
    private String providerCode;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getScenarioId() { return scenarioId; }
    public void setScenarioId(UUID scenarioId) { this.scenarioId = scenarioId; }
    public UUID getSlotId() { return slotId; }
    public void setSlotId(UUID slotId) { this.slotId = slotId; }
    public UUID getOpportunityId() { return opportunityId; }
    public void setOpportunityId(UUID opportunityId) { this.opportunityId = opportunityId; }
    public Integer getStartWeek() { return startWeek; }
    public void setStartWeek(Integer startWeek) { this.startWeek = startWeek; }
    public Integer getEndWeek() { return endWeek; }
    public void setEndWeek(Integer endWeek) { this.endWeek = endWeek; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
}

@Entity
@Table(name = "cost_items")
class CostItem {
    @Id
    private UUID id;
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String label;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "as_of")
    private LocalDate asOf;
    @Column(name = "source_url")
    private String sourceUrl;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getScenarioId() { return scenarioId; }
    public void setScenarioId(UUID scenarioId) { this.scenarioId = scenarioId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getAsOf() { return asOf; }
    public void setAsOf(LocalDate asOf) { this.asOf = asOf; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
}

@Entity
@Table(name = "recommendations")
class Recommendation {
    @Id
    private UUID id;
    @Column(name = "plan_id", nullable = false)
    private UUID planId;
    @Column(name = "opportunity_id")
    private UUID opportunityId;
    @Column(nullable = false)
    private int rank;
    @Column(nullable = false)
    private String rationale;
    private Integer credits;
    @Column(name = "estimated_hours")
    private BigDecimal estimatedHours;
    @Column(name = "applies_to_slot")
    private String appliesToSlot;
    @Column(name = "weeks_saved")
    private Integer weeksSaved;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public UUID getOpportunityId() { return opportunityId; }
    public void setOpportunityId(UUID opportunityId) { this.opportunityId = opportunityId; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }
    public String getAppliesToSlot() { return appliesToSlot; }
    public void setAppliesToSlot(String appliesToSlot) { this.appliesToSlot = appliesToSlot; }
    public Integer getWeeksSaved() { return weeksSaved; }
    public void setWeeksSaved(Integer weeksSaved) { this.weeksSaved = weeksSaved; }
}
