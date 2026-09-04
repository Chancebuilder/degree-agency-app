package com.degreedean.equivalencies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.degreedean.academicwallet.AcademicAsset;
import com.degreedean.common.DomainEnums;
import com.degreedean.policies.PolicyRule;
import com.degreedean.programs.RequirementSlot;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MatchingPipelineTest {

    @Test
    void tableLookupAssignsBestFitAndLeavesUnmatchedUnapplied() {
        RequirementSlot stats = slot("STAT", "Statistics", 3, false);
        RequirementSlot capstone = slot("STRAT", "Capstone", 3, true);
        AcademicAsset sophia = asset("PROVIDER_COURSE", "SOPH-STAT1001");
        AcademicAsset unknown = asset("INSTITUTION_COURSE", "ZZZ999");
        Equivalency eq = equivalency("PROVIDER_COURSE", "SOPH-STAT1001", stats.getId());

        MatchingPipeline.MatchResult result = new MatchingPipeline().execute(input(
                List.of(sophia, unknown),
                List.of(stats, capstone),
                List.of(eq),
                List.of(policy("MAX_TRANSFER_CREDITS", "90"))
        ));

        assertEquals(3, result.appliedCredits());
        assertEquals(1, result.applied().size());
        assertEquals(DomainEnums.APPLIED, result.applied().getFirst().classification());
        assertTrue(result.unapplied().stream().anyMatch(a -> a.asset().getId().equals(unknown.getId())));
        assertTrue(result.remaining().stream().anyMatch(s -> s.getId().equals(capstone.getId())));
    }

    @Test
    void residencySlotCannotBeFilledByTransfer() {
        RequirementSlot capstone = slot("STRAT", "Capstone", 3, true);
        AcademicAsset asset = asset("PROVIDER_COURSE", "SOPH-BUS9999");
        Equivalency eq = equivalency("PROVIDER_COURSE", "SOPH-BUS9999", capstone.getId());

        MatchingPipeline.MatchResult result = new MatchingPipeline().execute(input(
                List.of(asset),
                List.of(capstone),
                List.of(eq),
                List.of(policy("MAX_TRANSFER_CREDITS", "90"))
        ));

        assertEquals(0, result.appliedCredits());
        assertEquals(DomainEnums.SURPLUS, result.surplus().getFirst().classification());
        assertTrue(result.surplus().getFirst().reason().contains("residency"));
    }

    @Test
    void transferCapMovesLowestPriorityMatchesToSurplus() {
        RequirementSlot one = slot("ENG1", "Comp I", 3, false);
        RequirementSlot two = slot("ELEC1", "Elective", 3, false);
        AcademicAsset a = asset("PROVIDER_COURSE", "SOPH-ENG1001");
        AcademicAsset b = asset("PROVIDER_COURSE", "SOPH-HIST1001");
        Equivalency eq1 = equivalency("PROVIDER_COURSE", "SOPH-ENG1001", one.getId());
        Equivalency eq2 = equivalency("PROVIDER_COURSE", "SOPH-HIST1001", two.getId());

        MatchingPipeline.MatchResult result = new MatchingPipeline().execute(input(
                List.of(a, b),
                List.of(one, two),
                List.of(eq1, eq2),
                List.of(policy("MAX_TRANSFER_CREDITS", "3"))
        ));

        assertEquals(3, result.appliedCredits());
        assertEquals(1, result.surplus().size());
        assertTrue(result.surplus().getFirst().reason().contains("maximum transfer"));
    }

    @Test
    void oneAssetFillsOnlyOneSlot() {
        RequirementSlot qr = slot("QR", "Algebra", 3, false);
        RequirementSlot elec = slot("ELEC1", "Elective", 3, false);
        AcademicAsset asset = asset("EXAM", "CLEP-CALG");
        Equivalency eq1 = equivalency("EXAM", "CLEP-CALG", qr.getId());
        Equivalency eq2 = equivalency("EXAM", "CLEP-CALG", elec.getId());

        MatchingPipeline.MatchResult result = new MatchingPipeline().execute(input(
                List.of(asset),
                List.of(qr, elec),
                List.of(eq1, eq2),
                List.of()
        ));

        assertEquals(1, result.applied().size());
        assertEquals("QR", result.applied().getFirst().slot().getSlotCode());
    }

    private MatchingPipeline.MatchInput input(
            List<AcademicAsset> assets,
            List<RequirementSlot> slots,
            List<Equivalency> eqs,
            List<PolicyRule> policies
    ) {
        return new MatchingPipeline.MatchInput(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "2025-2026",
                assets,
                slots,
                eqs,
                policies,
                eq -> DomainEnums.VERIFIED
        );
    }

    private RequirementSlot slot(String code, String title, int credits, boolean residency) {
        RequirementSlot slot = new RequirementSlot();
        slot.setId(UUID.nameUUIDFromBytes(code.getBytes()));
        slot.setSlotCode(code);
        slot.setTitle(title);
        slot.setCredits(credits);
        slot.setResidencyRequired(residency);
        slot.setGroupName(code.startsWith("ELEC") ? "Major" : "General Education");
        slot.setLevel("LOWER");
        return slot;
    }

    private AcademicAsset asset(String type, String identifier) {
        AcademicAsset asset = new AcademicAsset();
        asset.setId(UUID.nameUUIDFromBytes(identifier.getBytes()));
        asset.setSourceType(type);
        asset.setSourceIdentifier(identifier);
        asset.setCourseCode(identifier);
        asset.setCourseTitle(identifier);
        asset.setCredits(BigDecimal.valueOf(3));
        asset.setVerifiedByStudent(true);
        return asset;
    }

    private Equivalency equivalency(String type, String identifier, UUID slotId) {
        Equivalency eq = new Equivalency();
        eq.setId(UUID.randomUUID());
        eq.setSourceType(type);
        eq.setSourceIdentifier(identifier);
        eq.setProgramId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        eq.setCatalogYear("2025-2026");
        eq.setRequirementSlotId(slotId);
        eq.setStatus(DomainEnums.VERIFIED);
        return eq;
    }

    private PolicyRule policy(String type, String value) {
        PolicyRule rule = new PolicyRule();
        rule.setRuleType(type);
        rule.setRuleValue(value);
        return rule;
    }
}
