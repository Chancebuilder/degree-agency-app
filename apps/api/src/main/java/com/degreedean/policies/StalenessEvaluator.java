package com.degreedean.policies;

import com.degreedean.common.DomainEnums;
import com.degreedean.config.AppProperties;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Staleness degrades output. It never silently passes through.
 */
public class StalenessEvaluator {
    private final AppProperties.Staleness intervals;

    public StalenessEvaluator(AppProperties.Staleness intervals) {
        this.intervals = intervals;
    }

    public boolean isStale(LocalDate lastVerifiedAt, int intervalDays, LocalDate now) {
        if (lastVerifiedAt == null) {
            return true;
        }
        return ChronoUnit.DAYS.between(lastVerifiedAt, now) > intervalDays;
    }

    public boolean isPolicyStale(LocalDate lastVerifiedAt, LocalDate now) {
        return isStale(lastVerifiedAt, intervals.getPolicyDays(), now);
    }

    public boolean isTuitionStale(LocalDate lastVerifiedAt, LocalDate now) {
        return isStale(lastVerifiedAt, intervals.getTuitionDays(), now);
    }

    public boolean isPricingStale(LocalDate lastVerifiedAt, LocalDate now) {
        return isStale(lastVerifiedAt, intervals.getProviderPricingDays(), now);
    }

    public boolean isRequirementStale(LocalDate lastVerifiedAt, LocalDate now) {
        return isStale(lastVerifiedAt, intervals.getRequirementsDays(), now);
    }

    public boolean isEquivalencyStale(LocalDate lastVerifiedAt, LocalDate now) {
        return isStale(lastVerifiedAt, intervals.getEquivalencyDays(), now);
    }

    public boolean isRecommendationExpired(LocalDate expires, LocalDate now) {
        return expires != null && now.isAfter(expires);
    }

    /**
     * Confidence is derived from evidence, then capped by stale governing policy.
     */
    public String deriveConfidence(
            String equivalencyStatus,
            boolean categoryAccepted,
            boolean courseSpecificUnlisted,
            boolean governingPolicyStale,
            boolean recommendationExpired,
            boolean noPolicy
    ) {
        if (recommendationExpired) {
            return DomainEnums.NOT_ACCEPTED;
        }
        if (!categoryAccepted) {
            return DomainEnums.NOT_ACCEPTED;
        }
        if (noPolicy) {
            return DomainEnums.UNVERIFIED;
        }
        String base = equivalencyStatus;
        if (courseSpecificUnlisted && categoryAccepted) {
            base = DomainEnums.REQUIRES_CONFIRMATION;
        }
        if (governingPolicyStale) {
            return DomainEnums.REQUIRES_CONFIRMATION;
        }
        return base;
    }

    public boolean blocksOptimizationClaim(int staleOrUnverifiedApplied, int totalApplied, double maxRatio) {
        if (totalApplied == 0) {
            return false;
        }
        return ((double) staleOrUnverifiedApplied / (double) totalApplied) > maxRatio;
    }
}
