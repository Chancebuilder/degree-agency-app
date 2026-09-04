package com.degreedean.simulator;

import com.degreedean.common.DomainEnums;
import com.degreedean.config.AppProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SimulatorEngine {
    private final AppProperties.Simulator config;

    public SimulatorEngine(AppProperties.Simulator config) {
        this.config = config;
    }

    public double hoursPerCredit(WorkItem item) {
        if (item.hoursPerCreditOverride() != null) {
            return item.hoursPerCreditOverride();
        }
        if (item.providerDefaultHours() != null) {
            return item.providerDefaultHours();
        }
        return switch (item.category()) {
            case "SOPHIA" -> config.getHoursPerCredit().getSophiaTier();
            case "STUDYCOM", "STRAIGHTERLINE" -> config.getHoursPerCredit().getStudyStraighterTier();
            case "EXAM", "CLEP", "DSST" -> config.getHoursPerCredit().getExam();
            case "COMPETENCY_BASED" -> config.getHoursPerCredit().getCompetencyBased();
            case "PLA", "CERTIFICATION" -> config.getHoursPerCredit().getPla();
            default -> config.getHoursPerCredit().getTermBased();
        };
    }

    public double effortHours(WorkItem item) {
        if ("EXAM".equals(item.opportunityType()) || "CLEP".equals(item.category())) {
            if (item.effortHoursOverride() != null) {
                return item.effortHoursOverride();
            }
            return config.getHoursPerCredit().getExam();
        }
        double hours = item.credits() * hoursPerCredit(item);
        if ("PLA".equals(item.category())) {
            hours += config.getHoursPerCredit().getPlaOverhead();
        }
        return hours;
    }

    public double attritionBuffer(String intensity) {
        return switch (intensity == null ? "STANDARD" : intensity.toUpperCase(Locale.ROOT)) {
            case "CONSERVATIVE" -> config.getAttrition().getConservative();
            case "AGGRESSIVE" -> config.getAttrition().getAggressive();
            default -> config.getAttrition().getStandard();
        };
    }

    public double effectiveWeeklyHours(int declaredWeeklyHours, String intensity) {
        return declaredWeeklyHours * (1.0 - attritionBuffer(intensity));
    }

    public String feasibility(double loadRatio) {
        if (loadRatio <= 0.60) {
            return DomainEnums.COMFORTABLE;
        }
        if (loadRatio <= 0.90) {
            return DomainEnums.REALISTIC;
        }
        if (loadRatio <= 1.10) {
            return DomainEnums.AGGRESSIVE;
        }
        return DomainEnums.NOT_FEASIBLE;
    }

    public SimulationResult simulate(SimulationRequest request) {
        List<WorkItem> remaining = new ArrayList<>(request.items());
        remaining.sort(Comparator.comparing(WorkItem::residencyRequired)
                .thenComparing(WorkItem::credits)
                .reversed());

        int concurrency = Math.max(1, Math.min(4, request.maxConcurrent()));
        double weekly = effectiveWeeklyHours(request.weeklyHours(), request.intensity());
        int unavailable = request.unavailableWeeks();
        double totalEffort = remaining.stream().mapToDouble(this::effortHours).sum();

        List<ScheduledItem> schedule = new ArrayList<>();
        Map<String, Integer> providerWeeks = new HashMap<>();
        int week = 1;
        int index = 0;
        while (index < remaining.size()) {
            int batch = Math.min(concurrency, remaining.size() - index);
            double batchHours = 0;
            int start = week;
            for (int i = 0; i < batch; i++) {
                WorkItem item = remaining.get(index + i);
                batchHours += effortHours(item);
            }
            int weeksNeeded = (int) Math.max(1, Math.ceil(batchHours / Math.max(weekly, 0.5)));
            int end = start + weeksNeeded - 1;
            for (int i = 0; i < batch; i++) {
                WorkItem item = remaining.get(index + i);
                schedule.add(new ScheduledItem(item, start, end, itemCost(item, weeksNeeded)));
                providerWeeks.merge(item.providerCode(), weeksNeeded, Integer::sum);
            }
            week = end + 1;
            index += batch;
        }

        int rawWeeks = Math.max(0, week - 1);
        int calendarWeeks = rawWeeks + unavailable;
        LocalDate completion = request.startDate().plusWeeks(calendarWeeks);

        int availableWeeksToTarget = request.targetDate() == null
                ? calendarWeeks
                : (int) Math.max(1, java.time.temporal.ChronoUnit.WEEKS.between(request.startDate(), request.targetDate()));
        double requiredWeekly = totalEffort / Math.max(availableWeeksToTarget, 1);
        double loadRatio = requiredWeekly / Math.max(weekly, 0.5);
        String band = feasibility(loadRatio);

        BigDecimal oneTime = request.oneTimeFees();
        BigDecimal tuition = request.tuitionFn().apply(calendarWeeks);
        BigDecimal subscriptions = BigDecimal.ZERO;
        int subscriptionMonths = 0;
        for (Map.Entry<String, Integer> entry : providerWeeks.entrySet()) {
            BigDecimal monthly = request.monthlyPriceFn().apply(entry.getKey());
            if (monthly.signum() > 0) {
                int months = (int) Math.ceil(entry.getValue() / config.getWeeksPerSubscriptionMonth());
                subscriptionMonths += months;
                subscriptions = subscriptions.add(monthly.multiply(BigDecimal.valueOf(months)));
            }
        }
        BigDecimal courseFees = schedule.stream()
                .map(ScheduledItem::directCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = oneTime.add(tuition).add(subscriptions).add(courseFees)
                .setScale(2, RoundingMode.HALF_UP);

        String confidence = weaker(bandToConfidence(band), request.weakestCreditConfidence());
        return new SimulationResult(
                completion,
                total,
                calendarWeeks,
                subscriptionMonths,
                schedule.size(),
                band,
                confidence,
                loadRatio,
                requiredWeekly,
                schedule,
                subscriptions,
                tuition,
                oneTime.add(courseFees)
        );
    }

    public String weaker(String left, String right) {
        return rank(left) >= rank(right) ? left : right;
    }

    private int rank(String value) {
        return switch (value) {
            case DomainEnums.NOT_FEASIBLE, DomainEnums.NOT_ACCEPTED, DomainEnums.UNVERIFIED -> 3;
            case DomainEnums.AGGRESSIVE, DomainEnums.REQUIRES_CONFIRMATION -> 2;
            case DomainEnums.REALISTIC, DomainEnums.HIGH_CONFIDENCE -> 1;
            default -> 0;
        };
    }

    private String bandToConfidence(String band) {
        return switch (band) {
            case DomainEnums.COMFORTABLE -> DomainEnums.VERIFIED;
            case DomainEnums.REALISTIC -> DomainEnums.HIGH_CONFIDENCE;
            case DomainEnums.AGGRESSIVE -> DomainEnums.REQUIRES_CONFIRMATION;
            default -> DomainEnums.UNVERIFIED;
        };
    }

    private BigDecimal itemCost(WorkItem item, int weeks) {
        BigDecimal extra = item.examOrCourseFee() == null ? BigDecimal.ZERO : item.examOrCourseFee();
        return extra;
    }

    public record WorkItem(
            String slotCode,
            String title,
            int credits,
            boolean residencyRequired,
            String providerCode,
            String category,
            String opportunityType,
            Double hoursPerCreditOverride,
            Double providerDefaultHours,
            Double effortHoursOverride,
            BigDecimal examOrCourseFee
    ) {}

    public record ScheduledItem(WorkItem item, int startWeek, int endWeek, BigDecimal directCost) {}

    public record SimulationRequest(
            LocalDate startDate,
            LocalDate targetDate,
            int weeklyHours,
            String intensity,
            int maxConcurrent,
            int unavailableWeeks,
            List<WorkItem> items,
            BigDecimal oneTimeFees,
            java.util.function.IntFunction<BigDecimal> tuitionFn,
            java.util.function.Function<String, BigDecimal> monthlyPriceFn,
            String weakestCreditConfidence
    ) {}

    public record SimulationResult(
            LocalDate projectedCompletionDate,
            BigDecimal totalCost,
            int weeksRemaining,
            int subscriptionMonths,
            int courseCount,
            String feasibility,
            String confidence,
            double loadRatio,
            double requiredWeeklyHours,
            List<ScheduledItem> schedule,
            BigDecimal subscriptionCost,
            BigDecimal tuition,
            BigDecimal otherFees
    ) {}
}
