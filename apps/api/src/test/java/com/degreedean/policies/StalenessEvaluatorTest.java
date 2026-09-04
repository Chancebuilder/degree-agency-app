package com.degreedean.policies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.degreedean.common.DomainEnums;
import com.degreedean.config.AppProperties;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StalenessEvaluatorTest {
    private StalenessEvaluator evaluator;
    private final LocalDate now = LocalDate.of(2026, 9, 4);

    @BeforeEach
    void setUp() {
        evaluator = new StalenessEvaluator(new AppProperties.Staleness());
    }

    @Test
    void providerPricingStaleAfter30Days() {
        assertFalse(evaluator.isPricingStale(now.minusDays(30), now));
        assertTrue(evaluator.isPricingStale(now.minusDays(31), now));
    }

    @Test
    void tuitionStaleAfter90Days() {
        assertFalse(evaluator.isTuitionStale(now.minusDays(90), now));
        assertTrue(evaluator.isTuitionStale(now.minusDays(91), now));
    }

    @Test
    void policyStaleAfter180Days() {
        assertFalse(evaluator.isPolicyStale(now.minusDays(180), now));
        assertTrue(evaluator.isPolicyStale(now.minusDays(181), now));
    }

    @Test
    void requirementsStaleAfter180Days() {
        assertTrue(evaluator.isRequirementStale(now.minusDays(181), now));
    }

    @Test
    void equivalencyStaleAfter180Days() {
        assertTrue(evaluator.isEquivalencyStale(now.minusDays(181), now));
    }

    @Test
    void missingVerificationDateIsStale() {
        assertTrue(evaluator.isPolicyStale(null, now));
    }

    @Test
    void expiredRecommendationIsNotAccepted() {
        assertTrue(evaluator.isRecommendationExpired(now.minusDays(1), now));
        assertEquals(DomainEnums.NOT_ACCEPTED, evaluator.deriveConfidence(
                DomainEnums.VERIFIED, true, false, false, true, false));
    }

    @Test
    void stalePolicyCapsConfidenceAtRequiresConfirmation() {
        assertEquals(DomainEnums.REQUIRES_CONFIRMATION, evaluator.deriveConfidence(
                DomainEnums.VERIFIED, true, false, true, false, false));
    }

    @Test
    void excludedCategoryIsNotAccepted() {
        assertEquals(DomainEnums.NOT_ACCEPTED, evaluator.deriveConfidence(
                DomainEnums.VERIFIED, false, false, false, false, false));
    }

    @Test
    void noPolicyIsUnverified() {
        assertEquals(DomainEnums.UNVERIFIED, evaluator.deriveConfidence(
                DomainEnums.VERIFIED, true, false, false, false, true));
    }

    @Test
    void courseSpecificUnlistedRequiresConfirmation() {
        assertEquals(DomainEnums.REQUIRES_CONFIRMATION, evaluator.deriveConfidence(
                DomainEnums.VERIFIED, true, true, false, false, false));
    }

    @Test
    void optimizationBlockedOverTwentyPercent() {
        assertFalse(evaluator.blocksOptimizationClaim(1, 10, 0.20));
        assertTrue(evaluator.blocksOptimizationClaim(3, 10, 0.20));
    }
}
