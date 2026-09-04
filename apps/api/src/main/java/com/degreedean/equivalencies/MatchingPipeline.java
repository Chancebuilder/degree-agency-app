package com.degreedean.equivalencies;

import com.degreedean.academicwallet.AcademicAsset;
import com.degreedean.common.DomainEnums;
import com.degreedean.policies.PolicyRule;
import com.degreedean.programs.RequirementSlot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Deterministic table-lookup matching. No fuzzy matching, no inference.
 * Order: resolve → assign best-fit → apply caps → classify → remaining slots.
 */
public class MatchingPipeline {

    public MatchResult execute(MatchInput input) {
        List<ResolvedAsset> resolved = input.assets().stream()
                .map(asset -> resolve(asset, input))
                .toList();

        List<Assignment> candidates = new ArrayList<>();
        for (ResolvedAsset resolvedAsset : resolved) {
            if (resolvedAsset.matches().isEmpty()) {
                continue;
            }
            for (Equivalency eq : resolvedAsset.matches()) {
                RequirementSlot slot = input.slot(eq.getRequirementSlotId());
                if (slot == null) {
                    continue;
                }
                String confidence = input.confidenceFor(eq);
                if (DomainEnums.NOT_ACCEPTED.equals(confidence) || DomainEnums.POSSIBLE.equals(confidence)) {
                    continue;
                }
                candidates.add(new Assignment(resolvedAsset.asset(), eq, slot, confidence, null, null));
            }
        }

        candidates.sort(Comparator
                .comparing((Assignment a) -> a.slot().isResidencyRequired())
                .thenComparing(a -> "General Education".equals(a.slot().getGroupName()) ? 0 : 1)
                .thenComparing(a -> electiveRank(a.slot()))
                .thenComparing(a -> confidenceRank(a.confidence()))
                .thenComparing(a -> -a.slot().getCredits()));

        Set<UUID> usedAssets = new HashSet<>();
        Set<UUID> usedSlots = new HashSet<>();
        List<Assignment> applied = new ArrayList<>();
        List<Assignment> surplus = new ArrayList<>();
        List<Assignment> unapplied = new ArrayList<>();

        for (Assignment candidate : candidates) {
            if (usedAssets.contains(candidate.asset().getId()) || usedSlots.contains(candidate.slot().getId())) {
                continue;
            }
            if (candidate.slot().isResidencyRequired()) {
                surplus.add(candidate.with("SURPLUS",
                        "This slot must be completed in residency at the receiving institution."));
                continue;
            }
            usedAssets.add(candidate.asset().getId());
            usedSlots.add(candidate.slot().getId());
            applied.add(candidate.with(DomainEnums.APPLIED, "Matched to a verified equivalency."));
        }

        for (ResolvedAsset resolvedAsset : resolved) {
            if (!usedAssets.contains(resolvedAsset.asset().getId())
                    && applied.stream().noneMatch(a -> a.asset().getId().equals(resolvedAsset.asset().getId()))
                    && surplus.stream().noneMatch(a -> a.asset().getId().equals(resolvedAsset.asset().getId()))) {
                String reason = resolvedAsset.matches().isEmpty()
                        ? "No verified equivalency exists for this asset, this program, and this catalog year."
                        : "A match existed but another asset already filled the slot, or the slot is residency-only.";
                unapplied.add(new Assignment(resolvedAsset.asset(), null, null, DomainEnums.UNVERIFIED,
                        DomainEnums.UNAPPLIED, reason));
            }
        }

        int maxTransfer = input.intPolicy("MAX_TRANSFER_CREDITS", 90);
        int appliedCredits = applied.stream().mapToInt(a -> a.slot().getCredits()).sum();
        if (appliedCredits > maxTransfer) {
            List<Assignment> demotable = new ArrayList<>(applied);
            demotable.sort(Comparator.comparing((Assignment a) -> electiveRank(a.slot())).reversed()
                    .thenComparing(a -> confidenceRank(a.confidence())).reversed());
            int overflow = appliedCredits - maxTransfer;
            List<Assignment> kept = new ArrayList<>();
            for (Assignment assignment : demotable) {
                if (overflow > 0) {
                    surplus.add(assignment.with(DomainEnums.SURPLUS,
                            "Excluded by the maximum transfer cap of " + maxTransfer + " credits."));
                    overflow -= assignment.slot().getCredits();
                } else {
                    kept.add(assignment);
                }
            }
            applied = kept;
        }

        final List<Assignment> appliedFinal = applied;
        List<RequirementSlot> remaining = input.slots().stream()
                .filter(slot -> appliedFinal.stream().noneMatch(a -> a.slot().getId().equals(slot.getId())))
                .toList();

        int confirmed = appliedFinal.stream()
                .filter(a -> DomainEnums.VERIFIED.equals(a.confidence()) || DomainEnums.HIGH_CONFIDENCE.equals(a.confidence()))
                .mapToInt(a -> a.slot().getCredits())
                .sum();
        int needsConfirmation = appliedFinal.stream()
                .filter(a -> DomainEnums.REQUIRES_CONFIRMATION.equals(a.confidence()))
                .mapToInt(a -> a.slot().getCredits())
                .sum();

        return new MatchResult(appliedFinal, surplus, unapplied, remaining, confirmed, needsConfirmation,
                appliedFinal.stream().mapToInt(a -> a.slot().getCredits()).sum());
    }

    private ResolvedAsset resolve(AcademicAsset asset, MatchInput input) {
        String identifier = firstNonBlank(asset.getSourceIdentifier(), asset.getCourseCode());
        if (identifier == null) {
            return new ResolvedAsset(asset, List.of());
        }
        String sourceType = asset.getSourceType();
        List<Equivalency> matches = input.equivalencies().stream()
                .filter(eq -> eq.getSourceType().equalsIgnoreCase(sourceType)
                        && eq.getSourceIdentifier().equalsIgnoreCase(identifier)
                        && eq.getProgramId().equals(input.programId())
                        && eq.getCatalogYear().equalsIgnoreCase(input.catalogYear()))
                .toList();
        return new ResolvedAsset(asset, matches);
    }

    private static int electiveRank(RequirementSlot slot) {
        String code = slot.getSlotCode().toUpperCase(Locale.ROOT);
        if (code.contains("ELEC")) {
            return 2;
        }
        if ("Major".equals(slot.getGroupName())) {
            return 0;
        }
        return 1;
    }

    private static int confidenceRank(String confidence) {
        return switch (confidence) {
            case DomainEnums.VERIFIED -> 0;
            case DomainEnums.HIGH_CONFIDENCE -> 1;
            case DomainEnums.REQUIRES_CONFIRMATION -> 2;
            default -> 3;
        };
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    public record MatchInput(
            UUID programId,
            String catalogYear,
            List<AcademicAsset> assets,
            List<RequirementSlot> slots,
            List<Equivalency> equivalencies,
            List<PolicyRule> policies,
            ConfidenceFn confidenceFn
    ) {
        public RequirementSlot slot(UUID id) {
            return slots.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        }

        public String confidenceFor(Equivalency eq) {
            return confidenceFn.apply(eq);
        }

        public int intPolicy(String type, int fallback) {
            return policies.stream()
                    .filter(p -> type.equals(p.getRuleType()))
                    .map(PolicyRule::getRuleValue)
                    .findFirst()
                    .map(Integer::parseInt)
                    .orElse(fallback);
        }
    }

    @FunctionalInterface
    public interface ConfidenceFn {
        String apply(Equivalency equivalency);
    }

    public record ResolvedAsset(AcademicAsset asset, List<Equivalency> matches) {}

    public record Assignment(
            AcademicAsset asset,
            Equivalency equivalency,
            RequirementSlot slot,
            String confidence,
            String classification,
            String reason
    ) {
        public Assignment with(String nextClassification, String nextReason) {
            return new Assignment(asset, equivalency, slot, confidence, nextClassification, nextReason);
        }
    }

    public record MatchResult(
            List<Assignment> applied,
            List<Assignment> surplus,
            List<Assignment> unapplied,
            List<RequirementSlot> remaining,
            int confirmedCredits,
            int needsConfirmationCredits,
            int appliedCredits
    ) {}
}
