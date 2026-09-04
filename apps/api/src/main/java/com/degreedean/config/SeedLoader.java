package com.degreedean.config;

import com.degreedean.creditcatalog.CreditOpportunity;
import com.degreedean.creditcatalog.CreditProvider;
import com.degreedean.equivalencies.Equivalency;
import com.degreedean.institutions.Institution;
import com.degreedean.policies.PolicyRule;
import com.degreedean.programs.Program;
import com.degreedean.programs.RequirementSlot;
import com.opencsv.CSVReaderHeaderAware;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class SeedLoader implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(SeedLoader.class);

    private final AppProperties properties;
    @PersistenceContext
    private EntityManager entityManager;

    public SeedLoader(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!properties.getSeed().isOnStartup()) {
            return;
        }
        Path dir = Path.of(properties.getSeed().getPath()).toAbsolutePath().normalize();
        if (!Files.isDirectory(dir)) {
            Path fallback = Path.of("data/seed").toAbsolutePath();
            if (Files.isDirectory(fallback)) {
                dir = fallback;
            } else {
                log.warn("Seed directory not found at {} — catalog will be empty until seed is loaded", dir);
                return;
            }
        }
        log.info("Loading seed data from {}", dir);
        loadInstitutions(dir);
        loadPrograms(dir);
        loadSlots(dir);
        loadPolicies(dir);
        loadProviders(dir);
        loadOpportunities(dir);
        loadEquivalencies(dir);
        log.info("Seed load complete");
    }

    private void loadInstitutions(Path dir) throws Exception {
        read(dir.resolve("institutions.csv"), row -> {
            Institution institution = getOrNew(Institution.class, uuid(row.get("id")));
            boolean create = institution.getId() == null;
            institution.setId(uuid(row.get("id")));
            institution.setCode(row.get("code"));
            institution.setName(row.get("name"));
            institution.setShortName(row.get("short_name"));
            institution.setTier(row.get("tier"));
            institution.setDeliveryModel(row.get("delivery_model"));
            institution.setMarketplaceVisibilityLevel(intVal(row.get("marketplace_visibility_level"), 0));
            institution.setSourceUrl(row.get("source_url"));
            institution.setLastVerifiedAt(date(row.get("last_verified_at")));
            institution.setVerifiedBy(row.get("verified_by"));
            institution.setVerificationStatus(row.get("verification_status"));
            institution.setNotes(row.get("notes"));
            Instant now = Instant.now();
            if (create) {
                institution.setCreatedAt(now);
            }
            institution.setUpdatedAt(now);
            entityManager.merge(institution);
        });
    }

    private void loadPrograms(Path dir) throws Exception {
        read(dir.resolve("programs.csv"), row -> {
            Institution institution = findInstitution(row.get("institution_code"));
            Program program = getOrNew(Program.class, uuid(row.get("id")));
            program.setId(uuid(row.get("id")));
            program.setInstitutionId(institution.getId());
            program.setCode(row.get("code"));
            program.setName(row.get("name"));
            program.setDegreeFamily(row.get("degree_family"));
            program.setCatalogYear(row.get("catalog_year"));
            program.setTotalCredits(intVal(row.get("total_credits"), 120));
            program.setSourceUrl(row.get("source_url"));
            program.setLastVerifiedAt(date(row.get("last_verified_at")));
            program.setVerifiedBy(row.get("verified_by"));
            program.setVerificationStatus(row.get("verification_status"));
            program.setNotes(row.get("notes"));
            entityManager.merge(program);
        });
    }

    private void loadSlots(Path dir) throws Exception {
        read(dir.resolve("requirement_slots.csv"), row -> {
            Program program = findProgram(row.get("institution_code"), row.get("program_code"));
            RequirementSlot slot = getOrNew(RequirementSlot.class, uuid(row.get("id")));
            slot.setId(uuid(row.get("id")));
            slot.setProgramId(program.getId());
            slot.setSlotCode(row.get("slot_code"));
            slot.setTitle(row.get("title"));
            slot.setCredits(intVal(row.get("credits"), 3));
            slot.setLevel(row.get("level"));
            slot.setResidencyRequired(Boolean.parseBoolean(row.get("residency_required")));
            slot.setSortOrder(intVal(row.get("sort_order"), 0));
            slot.setGroupName(row.get("group_name"));
            entityManager.merge(slot);
        });
    }

    private void loadPolicies(Path dir) throws Exception {
        read(dir.resolve("policy_rules.csv"), row -> {
            Institution institution = findInstitution(row.get("institution_code"));
            Program program = findProgram(row.get("institution_code"), row.get("program_code"));
            PolicyRule rule = getOrNew(PolicyRule.class, uuid(row.get("id")));
            rule.setId(uuid(row.get("id")));
            rule.setInstitutionId(institution.getId());
            rule.setProgramId(program.getId());
            rule.setCatalogYear(row.get("catalog_year"));
            rule.setRuleType(row.get("rule_type"));
            rule.setRuleValue(row.get("rule_value"));
            rule.setUnit(row.get("unit"));
            rule.setEffectiveDate(date(row.get("effective_date")));
            rule.setExpirationDate(date(row.get("expiration_date")));
            rule.setSourceUrl(row.get("source_url"));
            rule.setLastVerifiedAt(date(row.get("last_verified_at")));
            rule.setVerifiedBy(row.get("verified_by"));
            rule.setVerificationStatus(row.get("verification_status"));
            rule.setNotes(row.get("notes"));
            entityManager.merge(rule);
        });
    }

    private void loadProviders(Path dir) throws Exception {
        read(dir.resolve("credit_providers.csv"), row -> {
            CreditProvider provider = getOrNew(CreditProvider.class, uuid(row.get("id")));
            provider.setId(uuid(row.get("id")));
            provider.setCode(row.get("code"));
            provider.setName(row.get("name"));
            provider.setDeliveryPlatform(row.get("delivery_platform"));
            provider.setEvaluator(row.get("evaluator"));
            provider.setDefaultHoursPerCredit(decimal(row.get("default_hours_per_credit")));
            provider.setPricingModel(row.get("pricing_model"));
            provider.setMonthlyPrice(decimal(row.get("monthly_price")));
            provider.setSourceUrl(row.get("source_url"));
            provider.setLastVerifiedAt(date(row.get("last_verified_at")));
            provider.setVerifiedBy(row.get("verified_by"));
            provider.setVerificationStatus(row.get("verification_status"));
            entityManager.merge(provider);
        });
    }

    private void loadOpportunities(Path dir) throws Exception {
        read(dir.resolve("credit_opportunities.csv"), row -> {
            CreditProvider provider = entityManager.createQuery(
                            "select p from CreditProvider p where p.code = :code", CreditProvider.class)
                    .setParameter("code", row.get("provider_code"))
                    .getSingleResult();
            CreditOpportunity opportunity = getOrNew(CreditOpportunity.class, uuid(row.get("id")));
            opportunity.setId(uuid(row.get("id")));
            opportunity.setProviderId(provider.getId());
            opportunity.setCode(row.get("code"));
            opportunity.setTitle(row.get("title"));
            opportunity.setOpportunityType(row.get("opportunity_type"));
            opportunity.setAceId(emptyToNull(row.get("ace_id")));
            opportunity.setNccrsId(emptyToNull(row.get("nccrs_id")));
            opportunity.setCredits(intVal(row.get("credits"), 3));
            opportunity.setLevel(row.get("level"));
            opportunity.setHoursPerCredit(nullableDecimal(row.get("hours_per_credit")));
            opportunity.setEffortHoursOverride(nullableDecimal(row.get("effort_hours_override")));
            opportunity.setPrice(decimal(row.get("price")));
            opportunity.setExamFee(decimal(row.get("exam_fee")));
            opportunity.setRecommendationStart(date(row.get("recommendation_start")));
            opportunity.setRecommendationExpires(date(row.get("recommendation_expires")));
            opportunity.setSourceUrl(row.get("source_url"));
            opportunity.setLastVerifiedAt(date(row.get("last_verified_at")));
            opportunity.setVerifiedBy(row.get("verified_by"));
            opportunity.setVerificationStatus(row.get("verification_status"));
            entityManager.merge(opportunity);
        });
    }

    private void loadEquivalencies(Path dir) throws Exception {
        read(dir.resolve("equivalencies.csv"), row -> {
            Institution institution = findInstitution(row.get("institution_code"));
            Program program = findProgram(row.get("institution_code"), row.get("program_code"));
            RequirementSlot slot = entityManager.createQuery(
                            "select s from RequirementSlot s where s.programId = :pid and s.slotCode = :code",
                            RequirementSlot.class)
                    .setParameter("pid", program.getId())
                    .setParameter("code", row.get("slot_code"))
                    .getSingleResult();
            Equivalency equivalency = getOrNew(Equivalency.class, uuid(row.get("id")));
            equivalency.setId(uuid(row.get("id")));
            equivalency.setSourceType(row.get("source_type"));
            equivalency.setSourceIdentifier(row.get("source_identifier"));
            equivalency.setInstitutionId(institution.getId());
            equivalency.setProgramId(program.getId());
            equivalency.setCatalogYear(row.get("catalog_year"));
            equivalency.setRequirementSlotId(slot.getId());
            equivalency.setStatus(row.get("status"));
            equivalency.setSourceUrl(row.get("source_url"));
            equivalency.setLastVerifiedAt(date(row.get("last_verified_at")));
            equivalency.setVerifiedBy(row.get("verified_by"));
            equivalency.setVerificationStatus(row.get("verification_status"));
            entityManager.merge(equivalency);
        });
    }

    private Institution findInstitution(String code) {
        return entityManager.createQuery("select i from Institution i where i.code = :code", Institution.class)
                .setParameter("code", code)
                .getSingleResult();
    }

    private Program findProgram(String institutionCode, String programCode) {
        Institution institution = findInstitution(institutionCode);
        return entityManager.createQuery(
                        "select p from Program p where p.institutionId = :iid and p.code = :code", Program.class)
                .setParameter("iid", institution.getId())
                .setParameter("code", programCode)
                .getSingleResult();
    }

    private <T> T getOrNew(Class<T> type, UUID id) {
        T existing = entityManager.find(type, id);
        if (existing != null) {
            return existing;
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void read(Path path, RowHandler handler) throws Exception {
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(path.toFile()))) {
            Map<String, String> row;
            while ((row = reader.readMap()) != null) {
                handler.handle(row);
            }
        }
    }

    private static UUID uuid(String value) {
        return UUID.fromString(value);
    }

    private static LocalDate date(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private static int intVal(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return Integer.parseInt(value);
    }

    private static BigDecimal decimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }

    private static BigDecimal nullableDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new BigDecimal(value);
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    @FunctionalInterface
    private interface RowHandler {
        void handle(Map<String, String> row) throws Exception;
    }
}
