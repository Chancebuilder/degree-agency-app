package com.degreedean.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.degreedean.common.DomainEnums;
import com.degreedean.config.AppProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimulatorEngineTest {
    private SimulatorEngine engine;

    @BeforeEach
    void setUp() {
        engine = new SimulatorEngine(new AppProperties.Simulator());
    }

    @Test
    void hoursPerCreditResolveOpportunityThenProviderThenCategory() {
        assertEquals(5, engine.hoursPerCredit(item("SOPHIA", 3, 5.0, 9.0, null)));
        assertEquals(9, engine.hoursPerCredit(item("STUDYCOM", 3, null, 9.0, null)));
        assertEquals(5, engine.hoursPerCredit(item("SOPHIA", 3, null, null, null)));
        assertEquals(40, engine.hoursPerCredit(item("TERM_BASED", 3, null, null, null)));
    }

    @Test
    void examEffortIsPerExamNotPerCredit() {
        SimulatorEngine.WorkItem exam = new SimulatorEngine.WorkItem(
                "STAT", "CLEP Stats", 6, false, "CLEP", "CLEP", "EXAM",
                null, null, 12.0, BigDecimal.valueOf(93));
        assertEquals(12.0, engine.effortHours(exam));
    }

    @Test
    void plaAddsPreparationOverhead() {
        SimulatorEngine.WorkItem pla = new SimulatorEngine.WorkItem(
                "ELEC1", "Portfolio", 3, false, "PLA", "PLA", "PLA",
                7.0, null, null, BigDecimal.valueOf(500));
        assertEquals(31.0, engine.effortHours(pla));
    }

    @Test
    void attritionBuffersMatchIntensityTable() {
        assertEquals(0.25, engine.attritionBuffer("CONSERVATIVE"));
        assertEquals(0.15, engine.attritionBuffer("STANDARD"));
        assertEquals(0.05, engine.attritionBuffer("AGGRESSIVE"));
        assertEquals(8.5, engine.effectiveWeeklyHours(10, "STANDARD"));
    }

    @Test
    void feasibilityBandsMatchLoadRatioTable() {
        assertEquals(DomainEnums.COMFORTABLE, engine.feasibility(0.60));
        assertEquals(DomainEnums.REALISTIC, engine.feasibility(0.90));
        assertEquals(DomainEnums.AGGRESSIVE, engine.feasibility(1.10));
        assertEquals(DomainEnums.NOT_FEASIBLE, engine.feasibility(1.11));
    }

    @Test
    void subscriptionMonthsUseConfiguredWeekDivisor() {
        SimulatorEngine.WorkItem course = item("SOPHIA", 3, 5.0, 5.0, null);
        SimulatorEngine.SimulationResult result = engine.simulate(request(List.of(course, course, course), 10, "STANDARD"));
        assertTrue(result.subscriptionMonths() >= 1);
        assertTrue(result.subscriptionCost().signum() > 0);
        assertEquals(DomainEnums.REQUIRES_CONFIRMATION, result.confidence());
    }

    @Test
    void weakestCreditConfidenceCapsScenarioConfidence() {
        SimulatorEngine.WorkItem course = item("SOPHIA", 3, 5.0, 5.0, null);
        var req = new SimulatorEngine.SimulationRequest(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2028, 9, 1),
                20,
                "CONSERVATIVE",
                2,
                0,
                List.of(course),
                BigDecimal.ZERO,
                weeks -> BigDecimal.ZERO,
                code -> BigDecimal.ZERO,
                DomainEnums.REQUIRES_CONFIRMATION
        );
        assertEquals(DomainEnums.REQUIRES_CONFIRMATION, engine.simulate(req).confidence());
    }

    private SimulatorEngine.SimulationRequest request(List<SimulatorEngine.WorkItem> items, int hours, String intensity) {
        return new SimulatorEngine.SimulationRequest(
                LocalDate.of(2026, 9, 1),
                null,
                hours,
                intensity,
                2,
                0,
                items,
                BigDecimal.ZERO,
                weeks -> BigDecimal.valueOf(1800),
                code -> "SOPHIA".equals(code) ? BigDecimal.valueOf(99) : BigDecimal.ZERO,
                DomainEnums.REQUIRES_CONFIRMATION
        );
    }

    private SimulatorEngine.WorkItem item(String category, int credits, Double hours, Double provider, Double overrideHours) {
        return new SimulatorEngine.WorkItem(
                "STAT", "Stats", credits, false, category, category, "ONLINE_COURSE",
                hours, provider, overrideHours, BigDecimal.ZERO);
    }
}
