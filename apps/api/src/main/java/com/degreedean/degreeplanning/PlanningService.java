package com.degreedean.degreeplanning;

import com.degreedean.academicwallet.AcademicAsset;
import com.degreedean.academicwallet.AcademicAssetRepository;
import com.degreedean.common.ApiException;
import com.degreedean.common.DomainEnums;
import com.degreedean.config.AppProperties;
import com.degreedean.creditcatalog.CreditOpportunity;
import com.degreedean.creditcatalog.CreditProvider;
import com.degreedean.equivalencies.Equivalency;
import com.degreedean.equivalencies.EquivalencyRepository;
import com.degreedean.equivalencies.MatchingPipeline;
import com.degreedean.institutions.Institution;
import com.degreedean.institutions.InstitutionRepository;
import com.degreedean.policies.PolicyRule;
import com.degreedean.policies.PolicyRuleRepository;
import com.degreedean.policies.StalenessEvaluator;
import com.degreedean.programs.Program;
import com.degreedean.programs.RequirementSlot;
import com.degreedean.simulator.SimulatorEngine;
import com.degreedean.studentprofile.StudentProfile;
import com.degreedean.studentprofile.StudentProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanningService {
    private final DegreeGoalRepository goals;
    private final DegreePlanRepository plans;
    private final PlanMatchRepository matches;
    private final PlanScenarioRepository scenarios;
    private final PlanCourseRepository planCourses;
    private final CostItemRepository costItems;
    private final RecommendationRepository recommendations;
    private final AcademicAssetRepository assets;
    private final StudentProfileRepository profiles;
    private final InstitutionRepository institutions;
    private final EquivalencyRepository equivalencies;
    private final PolicyRuleRepository policies;
    private final AppProperties properties;
    @PersistenceContext
    private EntityManager entityManager;

    public PlanningService(
            DegreeGoalRepository goals,
            DegreePlanRepository plans,
            PlanMatchRepository matches,
            PlanScenarioRepository scenarios,
            PlanCourseRepository planCourses,
            CostItemRepository costItems,
            RecommendationRepository recommendations,
            AcademicAssetRepository assets,
            StudentProfileRepository profiles,
            InstitutionRepository institutions,
            EquivalencyRepository equivalencies,
            PolicyRuleRepository policies,
            AppProperties properties
    ) {
        this.goals = goals;
        this.plans = plans;
        this.matches = matches;
        this.scenarios = scenarios;
        this.planCourses = planCourses;
        this.costItems = costItems;
        this.recommendations = recommendations;
        this.assets = assets;
        this.profiles = profiles;
        this.institutions = institutions;
        this.equivalencies = equivalencies;
        this.policies = policies;
        this.properties = properties;
    }

    @Transactional
    public Map<String, Object> createGoal(UUID userId, String degreeFamily, UUID institutionId, UUID programId) {
        if (goals.countByUserId(userId) >= 1) {
            throw ApiException.forbidden("Free tier allows 1 degree goal");
        }
        Institution institution = institutions.findById(institutionId)
                .orElseThrow(() -> ApiException.notFound("Institution not found"));
        if (!"A".equals(institution.getTier())) {
            throw ApiException.badRequest("Only Tier A institutions can be used in a plan");
        }
        Program program = entityManager.find(Program.class, programId);
        if (program == null || !program.getInstitutionId().equals(institutionId)) {
            throw ApiException.badRequest("Program does not belong to the selected institution");
        }
        if (!program.getDegreeFamily().equals(degreeFamily)) {
            throw ApiException.badRequest("Program does not match the selected degree family");
        }
        DegreeGoal goal = new DegreeGoal();
        goal.setId(UUID.randomUUID());
        goal.setUserId(userId);
        goal.setDegreeFamily(degreeFamily);
        goal.setTargetInstitutionId(institutionId);
        goal.setTargetProgramId(programId);
        goal.setCreatedAt(Instant.now());
        goals.save(goal);

        DegreePlan plan = new DegreePlan();
        plan.setId(UUID.randomUUID());
        plan.setUserId(userId);
        plan.setGoalId(goal.getId());
        plan.setProgramId(programId);
        plan.setCatalogYear(program.getCatalogYear());
        plan.setCreatedAt(Instant.now());
        plan.setUpdatedAt(Instant.now());
        plans.save(plan);
        return Map.of("goal", goal, "plan", plan);
    }

    public List<DegreeGoal> listGoals(UUID userId) {
        return goals.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<DegreePlan> listPlans(UUID userId) {
        return plans.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Map<String, Object> runMatch(UUID userId, UUID planId) {
        DegreePlan plan = requirePlan(userId, planId);
        Program program = entityManager.find(Program.class, plan.getProgramId());
        List<RequirementSlot> slots = entityManager.createQuery(
                        "select s from RequirementSlot s where s.programId = :id order by s.sortOrder", RequirementSlot.class)
                .setParameter("id", program.getId())
                .getResultList();
        List<AcademicAsset> wallet = assets.findByUserIdOrderByCreatedAtAsc(userId).stream()
                .filter(AcademicAsset::isVerifiedByStudent)
                .toList();
        List<Equivalency> eqs = equivalencies.findByProgramIdAndCatalogYear(program.getId(), plan.getCatalogYear());
        List<PolicyRule> rules = policies.findByProgramId(program.getId());
        StalenessEvaluator staleness = new StalenessEvaluator(properties.getStaleness());
        LocalDate now = LocalDate.now();
        MatchingPipeline pipeline = new MatchingPipeline();
        MatchingPipeline.MatchResult result = pipeline.execute(new MatchingPipeline.MatchInput(
                program.getId(),
                plan.getCatalogYear(),
                wallet,
                slots,
                eqs,
                rules,
                eq -> confidence(eq, rules, staleness, now)
        ));

        matches.deleteByPlanId(planId);
        persistAssignments(planId, result.applied());
        persistAssignments(planId, result.surplus());
        persistAssignments(planId, result.unapplied());
        plan.setUpdatedAt(Instant.now());
        plans.save(plan);
        return matchView(plan, program, result, rules, staleness, now);
    }

    public Map<String, Object> getMatch(UUID userId, UUID planId) {
        DegreePlan plan = requirePlan(userId, planId);
        Program program = entityManager.find(Program.class, plan.getProgramId());
        List<PlanMatch> stored = matches.findByPlanId(planId);
        if (stored.isEmpty()) {
            return runMatch(userId, planId);
        }
        List<RequirementSlot> slots = entityManager.createQuery(
                        "select s from RequirementSlot s where s.programId = :id order by s.sortOrder", RequirementSlot.class)
                .setParameter("id", program.getId())
                .getResultList();
        int confirmed = stored.stream().filter(m -> DomainEnums.APPLIED.equals(m.getClassification()))
                .filter(m -> DomainEnums.VERIFIED.equals(m.getConfidence()) || DomainEnums.HIGH_CONFIDENCE.equals(m.getConfidence()))
                .mapToInt(m -> slotCredits(m.getSlotId()))
                .sum();
        int needs = stored.stream().filter(m -> DomainEnums.APPLIED.equals(m.getClassification()))
                .filter(m -> DomainEnums.REQUIRES_CONFIRMATION.equals(m.getConfidence()))
                .mapToInt(m -> slotCredits(m.getSlotId()))
                .sum();
        int applied = stored.stream().filter(m -> DomainEnums.APPLIED.equals(m.getClassification()))
                .mapToInt(m -> slotCredits(m.getSlotId()))
                .sum();
        List<UUID> filled = stored.stream()
                .filter(m -> DomainEnums.APPLIED.equals(m.getClassification()) && m.getSlotId() != null)
                .map(PlanMatch::getSlotId)
                .toList();
        List<RequirementSlot> remaining = slots.stream().filter(s -> !filled.contains(s.getId())).toList();
        return Map.of(
                "planId", plan.getId(),
                "program", program,
                "appliedCredits", applied,
                "confirmedCredits", confirmed,
                "needsConfirmationCredits", needs,
                "remainingCredits", remaining.stream().mapToInt(RequirementSlot::getCredits).sum(),
                "progressPercent", Math.round(100.0 * applied / Math.max(program.getTotalCredits(), 1)),
                "matches", stored,
                "remainingSlots", remaining,
                "estimateQualifier", "Estimates only. The receiving institution is the final authority."
        );
    }

    @Transactional
    public Map<String, Object> simulate(UUID userId, UUID planId) {
        DegreePlan plan = requirePlan(userId, planId);
        if (matches.findByPlanId(planId).isEmpty()) {
            runMatch(userId, planId);
        }
        Program program = entityManager.find(Program.class, plan.getProgramId());
        Institution institution = institutions.findById(program.getInstitutionId()).orElseThrow();
        StudentProfile profile = profiles.findByUserId(userId)
                .orElseThrow(() -> ApiException.badRequest("Create a profile before simulating"));
        List<PolicyRule> rules = policies.findByProgramId(program.getId());
        List<PlanMatch> stored = matches.findByPlanId(planId);
        List<RequirementSlot> remaining = remainingSlots(program.getId(), stored);
        List<CreditOpportunity> opportunities = entityManager.createQuery(
                        "select o from CreditOpportunity o", CreditOpportunity.class)
                .getResultList();
        List<CreditProvider> providers = entityManager.createQuery(
                        "select p from CreditProvider p", CreditProvider.class)
                .getResultList();
        Map<UUID, CreditProvider> providerById = new HashMap<>();
        providers.forEach(p -> providerById.put(p.getId(), p));

        List<ChosenFill> fills = chooseFills(remaining, stored, opportunities, program);
        SimulatorEngine engine = new SimulatorEngine(properties.getSimulator());
        StalenessEvaluator staleness = new StalenessEvaluator(properties.getStaleness());
        LocalDate now = LocalDate.now();
        boolean reqStale = staleness.isRequirementStale(program.getLastVerifiedAt(), now);
        boolean tuitionStale = rules.stream()
                .filter(r -> "TUITION".equals(r.getRuleType()))
                .anyMatch(r -> staleness.isTuitionStale(r.getLastVerifiedAt(), now));
        int staleApplied = (int) stored.stream()
                .filter(m -> DomainEnums.APPLIED.equals(m.getClassification()))
                .filter(m -> DomainEnums.REQUIRES_CONFIRMATION.equals(m.getConfidence())
                        || DomainEnums.UNVERIFIED.equals(m.getConfidence()))
                .count();
        int appliedCount = (int) stored.stream().filter(m -> DomainEnums.APPLIED.equals(m.getClassification())).count();
        boolean blocked = reqStale || staleness.blocksOptimizationClaim(
                staleApplied, Math.max(appliedCount, 1), properties.getSimulator().getMaxStaleAppliedRatio());

        String weakest = stored.stream()
                .filter(m -> DomainEnums.APPLIED.equals(m.getClassification()))
                .map(PlanMatch::getConfidence)
                .min(Comparator.comparingInt(this::confRank))
                .orElse(DomainEnums.VERIFIED);

        LocalDate start = profile.getStartDate() == null ? LocalDate.now() : profile.getStartDate();
        BigDecimal oneTime = fee(rules, "APPLICATION_FEE").add(fee(rules, "GRADUATION_FEE"))
                .add(fee(rules, "TRANSCRIPT_EVALUATION_FEE"));
        PolicyRule tuition = rules.stream().filter(r -> "TUITION".equals(r.getRuleType())).findFirst().orElse(null);
        int termWeeks = rules.stream().filter(r -> "TERM_WEEKS".equals(r.getRuleType()))
                .map(r -> Integer.parseInt(r.getRuleValue())).findFirst().orElse(8);

        scenarios.deleteByPlanId(planId);
        recommendations.deleteByPlanId(planId);

        Map<String, SimulatorEngine.SimulationResult> built = new HashMap<>();
        String originalIntensity = profile.getIntensity();
        built.put(DomainEnums.CUSTOM, runOne(engine, fills, profile, start, oneTime, tuition, termWeeks, weakest, institution, providerById));
        profile.setIntensity("AGGRESSIVE");
        built.put(DomainEnums.FASTEST, runOne(engine, fastest(fills), profile, start, oneTime, tuition, termWeeks, weakest, institution, providerById));
        profile.setIntensity(originalIntensity);
        built.put(DomainEnums.CHEAPEST, runOne(engine, cheapest(fills), profile, start, oneTime, tuition, termWeeks, weakest, institution, providerById));
        built.put(DomainEnums.BEST_FIT, runOne(engine, bestFit(fills), profile, start, oneTime, tuition, termWeeks, weakest, institution, providerById));

        Map<String, Object> saved = new HashMap<>();
        for (Map.Entry<String, SimulatorEngine.SimulationResult> entry : built.entrySet()) {
            boolean typeBlocked = blocked && (DomainEnums.FASTEST.equals(entry.getKey()) || DomainEnums.CHEAPEST.equals(entry.getKey()));
            if (reqStale && (DomainEnums.FASTEST.equals(entry.getKey()) || DomainEnums.CHEAPEST.equals(entry.getKey()))) {
                typeBlocked = true;
            }
            PlanScenario scenario = persistScenario(planId, entry.getKey(), entry.getValue(), typeBlocked,
                    typeBlocked ? blockingReason(reqStale, staleApplied, appliedCount) : null,
                    fills, tuitionStale, rules);
            saved.put(entry.getKey(), scenarioView(scenario, entry.getValue(), tuitionStale));
        }

        persistNba(planId, fills, built.get(DomainEnums.BEST_FIT));
        return Map.of(
                "planId", planId,
                "blockedOptimization", blocked,
                "blockingReason", blocked ? blockingReason(reqStale, staleApplied, appliedCount) : "",
                "estimateQualifier", "These figures are estimates as of the verification dates shown. The Degree Agency does not guarantee admission, credit acceptance, or transfer outcomes.",
                "scenarios", saved,
                "nextBestActions", recommendations.findByPlanIdOrderByRankAsc(planId)
        );
    }

    public Map<String, Object> dashboard(UUID userId) {
        StudentProfile profile = profiles.findByUserId(userId).orElse(null);
        List<DegreePlan> userPlans = plans.findByUserIdOrderByCreatedAtDesc(userId);
        if (userPlans.isEmpty()) {
            return Map.of("ready", false, "profile", profile, "message", "Choose a degree goal to build your first plan.");
        }
        DegreePlan plan = userPlans.getFirst();
        Map<String, Object> match = getMatch(userId, plan.getId());
        List<PlanScenario> latest = scenarios.findByPlanIdOrderByCreatedAtDesc(plan.getId());
        List<Recommendation> nba = recommendations.findByPlanIdOrderByRankAsc(plan.getId());
        Program program = entityManager.find(Program.class, plan.getProgramId());
        Institution institution = institutions.findById(program.getInstitutionId()).orElseThrow();
        return Map.of(
                "ready", true,
                "profile", profile,
                "institution", institution,
                "program", program,
                "plan", plan,
                "match", match,
                "scenarios", latest,
                "nextBestActions", nba,
                "estimateQualifier", "Estimates only. Final authority rests with the receiving institution."
        );
    }

    private SimulatorEngine.SimulationResult runOne(
            SimulatorEngine engine,
            List<ChosenFill> fills,
            StudentProfile profile,
            LocalDate start,
            BigDecimal oneTime,
            PolicyRule tuition,
            int termWeeks,
            String weakest,
            Institution institution,
            Map<UUID, CreditProvider> providerById
    ) {
        List<SimulatorEngine.WorkItem> items = fills.stream().map(fill -> {
            CreditProvider provider = fill.opportunity() == null ? null : providerById.get(fill.opportunity().getProviderId());
            boolean residency = fill.slot().isResidencyRequired() || fill.opportunity() == null;
            String category = residency ? institution.getDeliveryModel() : (provider == null ? "TERM_BASED" : provider.getCode());
            return new SimulatorEngine.WorkItem(
                    fill.slot().getSlotCode(),
                    fill.slot().getTitle(),
                    fill.slot().getCredits(),
                    residency,
                    provider == null ? institution.getCode() : provider.getCode(),
                    category,
                    fill.opportunity() == null ? "UNIVERSITY" : fill.opportunity().getOpportunityType(),
                    fill.opportunity() == null || fill.opportunity().getHoursPerCredit() == null
                            ? null : fill.opportunity().getHoursPerCredit().doubleValue(),
                    provider == null ? null : provider.getDefaultHoursPerCredit().doubleValue(),
                    fill.opportunity() == null || fill.opportunity().getEffortHoursOverride() == null
                            ? null : fill.opportunity().getEffortHoursOverride().doubleValue(),
                    fill.opportunity() == null ? BigDecimal.ZERO
                            : fill.opportunity().getPrice().add(fill.opportunity().getExamFee())
            );
        }).toList();
        BigDecimal monthlyDefault = BigDecimal.ZERO;
        return engine.simulate(new SimulatorEngine.SimulationRequest(
                start,
                profile.getTargetGraduationDate(),
                profile.getWeeklyStudyHours(),
                profile.getIntensity(),
                profile.getMaxConcurrent(),
                0,
                items,
                oneTime,
                weeks -> institutionalTuition(tuition, termWeeks, weeks, institution.getDeliveryModel()),
                code -> providersMonthly(code),
                weakest
        ));
    }

    private BigDecimal providersMonthly(String code) {
        List<CreditProvider> found = entityManager.createQuery(
                        "select p from CreditProvider p where p.code = :code", CreditProvider.class)
                .setParameter("code", code)
                .getResultList();
        if (found.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return found.getFirst().getMonthlyPrice();
    }

    private BigDecimal institutionalTuition(PolicyRule tuition, int termWeeks, int weeks, String delivery) {
        if (tuition == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal value = new BigDecimal(tuition.getRuleValue());
        if ("PER_CREDIT".equals(tuition.getUnit())) {
            return value.multiply(BigDecimal.valueOf(15)).setScale(2, RoundingMode.HALF_UP);
        }
        int terms = Math.max(1, (int) Math.ceil(weeks / (double) Math.max(termWeeks, 1)));
        return value.multiply(BigDecimal.valueOf(terms)).setScale(2, RoundingMode.HALF_UP);
    }

    private List<ChosenFill> chooseFills(
            List<RequirementSlot> remaining,
            List<PlanMatch> stored,
            List<CreditOpportunity> opportunities,
            Program program
    ) {
        List<Equivalency> eqs = equivalencies.findByProgramIdAndCatalogYear(program.getId(), program.getCatalogYear());
        List<ChosenFill> fills = new ArrayList<>();
        for (RequirementSlot slot : remaining) {
            if (slot.isResidencyRequired()) {
                fills.add(new ChosenFill(slot, null, null));
                continue;
            }
            Equivalency match = eqs.stream()
                    .filter(eq -> eq.getRequirementSlotId().equals(slot.getId()))
                    .filter(eq -> !"INSTITUTION_COURSE".equals(eq.getSourceType()))
                    .min(Comparator.comparing(eq -> providerPreference(eq.getSourceIdentifier())))
                    .orElse(null);
            CreditOpportunity opportunity = match == null ? null : opportunities.stream()
                    .filter(o -> o.getCode().equalsIgnoreCase(match.getSourceIdentifier()))
                    .findFirst().orElse(null);
            fills.add(new ChosenFill(slot, match, opportunity));
        }
        return fills;
    }

    private int providerPreference(String code) {
        if (code.startsWith("SOPH-")) return 0;
        if (code.startsWith("CLEP-")) return 1;
        if (code.startsWith("SDC-")) return 2;
        return 3;
    }

    private List<ChosenFill> fastest(List<ChosenFill> fills) {
        return fills.stream()
                .sorted(Comparator.comparing((ChosenFill f) -> f.opportunity() == null ? 50 : effort(f)))
                .toList();
    }

    private List<ChosenFill> cheapest(List<ChosenFill> fills) {
        return fills.stream()
                .sorted(Comparator.comparing(f -> f.opportunity() == null ? BigDecimal.valueOf(9999)
                        : f.opportunity().getPrice().add(f.opportunity().getExamFee())))
                .toList();
    }

    private List<ChosenFill> bestFit(List<ChosenFill> fills) {
        return fills;
    }

    private double effort(ChosenFill fill) {
        if (fill.opportunity() == null) {
            return 40 * fill.slot().getCredits();
        }
        if (fill.opportunity().getEffortHoursOverride() != null) {
            return fill.opportunity().getEffortHoursOverride().doubleValue();
        }
        if (fill.opportunity().getHoursPerCredit() != null) {
            return fill.opportunity().getHoursPerCredit().doubleValue() * fill.slot().getCredits();
        }
        return 9 * fill.slot().getCredits();
    }

    private PlanScenario persistScenario(
            UUID planId,
            String type,
            SimulatorEngine.SimulationResult result,
            boolean blocked,
            String reason,
            List<ChosenFill> fills,
            boolean tuitionStale,
            List<PolicyRule> rules
    ) {
        PlanScenario scenario = new PlanScenario();
        scenario.setId(UUID.randomUUID());
        scenario.setPlanId(planId);
        scenario.setScenarioType(type);
        scenario.setProjectedCompletionDate(result.projectedCompletionDate());
        scenario.setTotalCost(result.totalCost());
        scenario.setWeeksRemaining(result.weeksRemaining());
        scenario.setSubscriptionMonths(result.subscriptionMonths());
        scenario.setCourseCount(result.courseCount());
        scenario.setFeasibility(result.feasibility());
        scenario.setConfidence(blocked ? DomainEnums.REQUIRES_CONFIRMATION : result.confidence());
        scenario.setStaleBlocked(blocked);
        scenario.setBlockedReason(reason);
        scenario.setCreatedAt(Instant.now());
        scenarios.save(scenario);

        PolicyRule tuition = rules.stream().filter(r -> "TUITION".equals(r.getRuleType())).findFirst().orElse(null);
        costItems.save(item(scenario.getId(), "TUITION", "Institutional tuition", result.tuition(),
                tuition == null ? null : tuition.getLastVerifiedAt(), tuition == null ? null : tuition.getSourceUrl()));
        costItems.save(item(scenario.getId(), "SUBSCRIPTION", "Alternative-credit subscriptions", result.subscriptionCost(),
                LocalDate.now(), null));
        costItems.save(item(scenario.getId(), "FEES", "Application, exams, and course fees", result.otherFees(),
                LocalDate.now(), null));
        return scenario;
    }

    private CostItem item(UUID scenarioId, String category, String label, BigDecimal amount, LocalDate asOf, String url) {
        CostItem item = new CostItem();
        item.setId(UUID.randomUUID());
        item.setScenarioId(scenarioId);
        item.setCategory(category);
        item.setLabel(label);
        item.setAmount(amount);
        item.setAsOf(asOf);
        item.setSourceUrl(url);
        return item;
    }

    private Map<String, Object> scenarioView(PlanScenario scenario, SimulatorEngine.SimulationResult result, boolean tuitionStale) {
        Map<String, Object> view = new HashMap<>();
        view.put("scenario", scenario);
        view.put("asOfNote", tuitionStale
                ? "Tuition used in this estimate is stale and cannot support a cheapest-path claim until re-verified."
                : "Tuition figure reflects the last verified institutional rate.");
        view.put("loadRatio", result.loadRatio());
        view.put("requiredWeeklyHours", result.requiredWeeklyHours());
        view.put("costItems", costItems.findByScenarioId(scenario.getId()));
        return view;
    }

    private void persistNba(UUID planId, List<ChosenFill> fills, SimulatorEngine.SimulationResult bestFit) {
        int rank = 1;
        for (ChosenFill fill : fills) {
            if (fill.opportunity() == null) {
                continue;
            }
            if (fill.opportunity().getRecommendationExpires() != null
                    && LocalDate.now().isAfter(fill.opportunity().getRecommendationExpires())) {
                continue;
            }
            Recommendation rec = new Recommendation();
            rec.setId(UUID.randomUUID());
            rec.setPlanId(planId);
            rec.setOpportunityId(fill.opportunity().getId());
            rec.setRank(rank++);
            rec.setCredits(fill.opportunity().getCredits());
            rec.setEstimatedHours(fill.opportunity().getHoursPerCredit() == null
                    ? BigDecimal.valueOf(fill.slot().getCredits() * 9L)
                    : fill.opportunity().getHoursPerCredit().multiply(BigDecimal.valueOf(fill.slot().getCredits())));
            rec.setAppliesToSlot(fill.slot().getTitle());
            rec.setWeeksSaved(2);
            rec.setRationale("Complete " + fill.opportunity().getTitle() + " (" + fill.opportunity().getCredits()
                    + " credits). Applies to " + fill.slot().getTitle()
                    + ". Estimated " + rec.getEstimatedHours() + " hours.");
            recommendations.save(rec);
            if (rank > 3) {
                break;
            }
        }
    }

    private String blockingReason(boolean reqStale, int staleApplied, int appliedCount) {
        if (reqStale) {
            return "The requirement list is stale, so Fastest and Cheapest claims are withheld.";
        }
        return staleApplied + " of " + appliedCount
                + " applied credits depend on stale or unverified records (limit 20%).";
    }

    private void persistAssignments(UUID planId, List<MatchingPipeline.Assignment> assignments) {
        for (MatchingPipeline.Assignment assignment : assignments) {
            PlanMatch match = new PlanMatch();
            match.setId(UUID.randomUUID());
            match.setPlanId(planId);
            match.setAssetId(assignment.asset() == null ? null : assignment.asset().getId());
            match.setEquivalencyId(assignment.equivalency() == null ? null : assignment.equivalency().getId());
            match.setSlotId(assignment.slot() == null ? null : assignment.slot().getId());
            match.setClassification(assignment.classification());
            match.setReason(assignment.reason());
            match.setConfidence(assignment.confidence());
            matches.save(match);
        }
    }

    private Map<String, Object> matchView(
            DegreePlan plan,
            Program program,
            MatchingPipeline.MatchResult result,
            List<PolicyRule> rules,
            StalenessEvaluator staleness,
            LocalDate now
    ) {
        Map<String, Object> view = new HashMap<>();
        view.put("planId", plan.getId());
        view.put("program", program);
        view.put("appliedCredits", result.appliedCredits());
        view.put("confirmedCredits", result.confirmedCredits());
        view.put("needsConfirmationCredits", result.needsConfirmationCredits());
        view.put("remainingCredits", result.remaining().stream().mapToInt(RequirementSlot::getCredits).sum());
        view.put("progressPercent", Math.round(100.0 * result.appliedCredits() / Math.max(program.getTotalCredits(), 1)));
        view.put("applied", result.applied());
        view.put("surplus", result.surplus());
        view.put("unapplied", result.unapplied());
        view.put("remainingSlots", result.remaining());
        view.put("policies", rules);
        view.put("estimateQualifier", "Estimates only. The receiving institution is the final authority.");
        return view;
    }

    private List<RequirementSlot> remainingSlots(UUID programId, List<PlanMatch> stored) {
        List<RequirementSlot> slots = entityManager.createQuery(
                        "select s from RequirementSlot s where s.programId = :id order by s.sortOrder", RequirementSlot.class)
                .setParameter("id", programId)
                .getResultList();
        List<UUID> filled = stored.stream()
                .filter(m -> DomainEnums.APPLIED.equals(m.getClassification()) && m.getSlotId() != null)
                .map(PlanMatch::getSlotId)
                .toList();
        return slots.stream().filter(s -> !filled.contains(s.getId())).toList();
    }

    private String confidence(Equivalency eq, List<PolicyRule> rules, StalenessEvaluator staleness, LocalDate now) {
        boolean policyStale = rules.stream().anyMatch(r -> staleness.isPolicyStale(r.getLastVerifiedAt(), now)
                && (r.getRuleType().contains("ACE") || r.getRuleType().contains("NCCRS") || r.getRuleType().contains("TRANSFER")));
        boolean eqStale = staleness.isEquivalencyStale(eq.getLastVerifiedAt(), now);
        return staleness.deriveConfidence(
                eqStale ? DomainEnums.REQUIRES_CONFIRMATION : eq.getStatus(),
                true,
                false,
                policyStale,
                false,
                rules.isEmpty()
        );
    }

    private int slotCredits(UUID slotId) {
        if (slotId == null) {
            return 0;
        }
        RequirementSlot slot = entityManager.find(RequirementSlot.class, slotId);
        return slot == null ? 0 : slot.getCredits();
    }

    private BigDecimal fee(List<PolicyRule> rules, String type) {
        return rules.stream().filter(r -> type.equals(r.getRuleType()))
                .findFirst()
                .map(r -> new BigDecimal(r.getRuleValue()))
                .orElse(BigDecimal.ZERO);
    }

    private DegreePlan requirePlan(UUID userId, UUID planId) {
        DegreePlan plan = plans.findById(planId).orElseThrow(() -> ApiException.notFound("Plan not found"));
        if (!plan.getUserId().equals(userId)) {
            throw ApiException.forbidden("This plan belongs to another account");
        }
        return plan;
    }

    private int confRank(String confidence) {
        return switch (confidence) {
            case DomainEnums.UNVERIFIED, DomainEnums.NOT_ACCEPTED -> 0;
            case DomainEnums.REQUIRES_CONFIRMATION -> 1;
            case DomainEnums.HIGH_CONFIDENCE -> 2;
            default -> 3;
        };
    }

    private record ChosenFill(RequirementSlot slot, Equivalency equivalency, CreditOpportunity opportunity) {}
}
