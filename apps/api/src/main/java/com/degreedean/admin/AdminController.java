package com.degreedean.admin;

import com.degreedean.audit.AuditEvent;
import com.degreedean.audit.AuditEventRepository;
import com.degreedean.audit.AuditService;
import com.degreedean.common.ApiException;
import com.degreedean.equivalencies.Equivalency;
import com.degreedean.equivalencies.EquivalencyRepository;
import com.degreedean.policies.PolicyRule;
import com.degreedean.policies.PolicyRuleRepository;
import com.degreedean.policies.StalenessEvaluator;
import com.degreedean.config.AppProperties;
import com.degreedean.programs.Program;
import com.degreedean.creditcatalog.CreditOpportunity;
import com.degreedean.creditcatalog.CreditProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final PolicyRuleRepository policies;
    private final EquivalencyRepository equivalencies;
    private final AuditEventRepository auditEvents;
    private final AuditService auditService;
    private final AppProperties properties;
    @PersistenceContext
    private EntityManager entityManager;

    public AdminController(
            PolicyRuleRepository policies,
            EquivalencyRepository equivalencies,
            AuditEventRepository auditEvents,
            AuditService auditService,
            AppProperties properties
    ) {
        this.policies = policies;
        this.equivalencies = equivalencies;
        this.auditEvents = auditEvents;
        this.auditService = auditService;
        this.properties = properties;
    }

    @GetMapping("/policies")
    public List<PolicyRule> policies() {
        return policies.findAll();
    }

    @PostMapping("/policies")
    @Transactional
    public PolicyRule createPolicy(Authentication auth, @Valid @RequestBody PolicyRequest request) {
        PolicyRule rule = new PolicyRule();
        rule.setId(UUID.randomUUID());
        apply(rule, request);
        rule.setVersion(1);
        policies.save(rule);
        auditService.record(UUID.fromString(auth.getName()), "policy_rule", rule.getId(), "CREATE",
                null, request.ruleType() + "=" + request.ruleValue(), request.sourceUrl());
        return rule;
    }

    @PutMapping("/policies/{id}")
    @Transactional
    public PolicyRule updatePolicy(Authentication auth, @PathVariable UUID id, @Valid @RequestBody PolicyRequest request) {
        PolicyRule rule = policies.findById(id).orElseThrow(() -> ApiException.notFound("Policy not found"));
        String before = rule.getRuleType() + "=" + rule.getRuleValue() + " status=" + rule.getVerificationStatus();
        apply(rule, request);
        rule.setVersion(rule.getVersion() + 1);
        policies.save(rule);
        auditService.record(UUID.fromString(auth.getName()), "policy_rule", rule.getId(), "UPDATE",
                before, request.ruleType() + "=" + request.ruleValue(), request.sourceUrl());
        return rule;
    }

    @GetMapping("/equivalencies")
    public List<Equivalency> equivalencies() {
        return equivalencies.findAll();
    }

    @PostMapping("/equivalencies")
    @Transactional
    public Equivalency createEquivalency(Authentication auth, @Valid @RequestBody EquivalencyRequest request) {
        Equivalency equivalency = new Equivalency();
        equivalency.setId(UUID.randomUUID());
        apply(equivalency, request);
        equivalency.setVersion(1);
        equivalencies.save(equivalency);
        auditService.record(UUID.fromString(auth.getName()), "equivalency", equivalency.getId(), "CREATE",
                null, request.sourceType() + ":" + request.sourceIdentifier(), request.sourceUrl());
        return equivalency;
    }

    @PutMapping("/equivalencies/{id}")
    @Transactional
    public Equivalency updateEquivalency(Authentication auth, @PathVariable UUID id, @Valid @RequestBody EquivalencyRequest request) {
        Equivalency equivalency = equivalencies.findById(id).orElseThrow(() -> ApiException.notFound("Equivalency not found"));
        String before = equivalency.getSourceIdentifier() + " -> " + equivalency.getRequirementSlotId();
        apply(equivalency, request);
        equivalency.setVersion(equivalency.getVersion() + 1);
        equivalencies.save(equivalency);
        auditService.record(UUID.fromString(auth.getName()), "equivalency", equivalency.getId(), "UPDATE",
                before, request.sourceIdentifier() + " -> " + request.requirementSlotId(), request.sourceUrl());
        return equivalency;
    }

    @GetMapping("/stale-queue")
    public List<Map<String, Object>> staleQueue() {
        StalenessEvaluator evaluator = new StalenessEvaluator(properties.getStaleness());
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> queue = new ArrayList<>();
        for (PolicyRule rule : policies.findAll()) {
            boolean stale = "TUITION".equals(rule.getRuleType())
                    ? evaluator.isTuitionStale(rule.getLastVerifiedAt(), now)
                    : evaluator.isPolicyStale(rule.getLastVerifiedAt(), now);
            if (stale) {
                queue.add(Map.of(
                        "kind", "POLICY",
                        "id", rule.getId(),
                        "label", rule.getRuleType() + " " + rule.getRuleValue(),
                        "lastVerifiedAt", String.valueOf(rule.getLastVerifiedAt()),
                        "sourceUrl", rule.getSourceUrl() == null ? "" : rule.getSourceUrl(),
                        "impact", 1
                ));
            }
        }
        for (Equivalency equivalency : equivalencies.findAll()) {
            if (evaluator.isEquivalencyStale(equivalency.getLastVerifiedAt(), now)) {
                queue.add(Map.of(
                        "kind", "EQUIVALENCY",
                        "id", equivalency.getId(),
                        "label", equivalency.getSourceType() + " " + equivalency.getSourceIdentifier(),
                        "lastVerifiedAt", String.valueOf(equivalency.getLastVerifiedAt()),
                        "sourceUrl", equivalency.getSourceUrl() == null ? "" : equivalency.getSourceUrl(),
                        "impact", 1
                ));
            }
        }
        for (Program program : entityManager.createQuery("select p from Program p", Program.class).getResultList()) {
            if (evaluator.isRequirementStale(program.getLastVerifiedAt(), now)) {
                queue.add(Map.of(
                        "kind", "REQUIREMENTS",
                        "id", program.getId(),
                        "label", program.getName(),
                        "lastVerifiedAt", String.valueOf(program.getLastVerifiedAt()),
                        "sourceUrl", program.getSourceUrl() == null ? "" : program.getSourceUrl(),
                        "impact", 3
                ));
            }
        }
        for (CreditProvider provider : entityManager.createQuery("select p from CreditProvider p", CreditProvider.class).getResultList()) {
            if (evaluator.isPricingStale(provider.getLastVerifiedAt(), now)) {
                queue.add(Map.of(
                        "kind", "PROVIDER_PRICING",
                        "id", provider.getId(),
                        "label", provider.getName(),
                        "lastVerifiedAt", String.valueOf(provider.getLastVerifiedAt()),
                        "sourceUrl", provider.getSourceUrl() == null ? "" : provider.getSourceUrl(),
                        "impact", 2
                ));
            }
        }
        for (CreditOpportunity opportunity : entityManager.createQuery("select o from CreditOpportunity o", CreditOpportunity.class).getResultList()) {
            if (evaluator.isRecommendationExpired(opportunity.getRecommendationExpires(), now)) {
                queue.add(Map.of(
                        "kind", "EXPIRED_RECOMMENDATION",
                        "id", opportunity.getId(),
                        "label", opportunity.getTitle(),
                        "lastVerifiedAt", String.valueOf(opportunity.getRecommendationExpires()),
                        "sourceUrl", opportunity.getSourceUrl() == null ? "" : opportunity.getSourceUrl(),
                        "impact", 2
                ));
            }
        }
        queue.sort(Comparator.comparing((Map<String, Object> row) -> (Integer) row.get("impact")).reversed());
        return queue;
    }

    @GetMapping("/audit")
    public List<AuditEvent> audit() {
        return auditEvents.findTop200ByOrderByCreatedAtDesc();
    }

    private void apply(PolicyRule rule, PolicyRequest request) {
        rule.setInstitutionId(request.institutionId());
        rule.setProgramId(request.programId());
        rule.setCatalogYear(request.catalogYear());
        rule.setRuleType(request.ruleType());
        rule.setRuleValue(request.ruleValue());
        rule.setUnit(request.unit());
        rule.setEffectiveDate(request.effectiveDate());
        rule.setExpirationDate(request.expirationDate());
        rule.setSourceUrl(request.sourceUrl());
        rule.setLastVerifiedAt(request.lastVerifiedAt() == null ? LocalDate.now() : request.lastVerifiedAt());
        rule.setVerifiedBy(request.verifiedBy());
        rule.setVerificationStatus(request.verificationStatus());
        rule.setNotes(request.notes());
    }

    private void apply(Equivalency equivalency, EquivalencyRequest request) {
        equivalency.setSourceType(request.sourceType());
        equivalency.setSourceIdentifier(request.sourceIdentifier());
        equivalency.setInstitutionId(request.institutionId());
        equivalency.setProgramId(request.programId());
        equivalency.setCatalogYear(request.catalogYear());
        equivalency.setRequirementSlotId(request.requirementSlotId());
        equivalency.setStatus(request.status());
        equivalency.setSourceUrl(request.sourceUrl());
        equivalency.setLastVerifiedAt(request.lastVerifiedAt() == null ? LocalDate.now() : request.lastVerifiedAt());
        equivalency.setVerifiedBy(request.verifiedBy());
        equivalency.setVerificationStatus(request.verificationStatus());
    }

    public record PolicyRequest(
            UUID institutionId,
            UUID programId,
            @NotBlank String catalogYear,
            @NotBlank String ruleType,
            @NotBlank String ruleValue,
            @NotBlank String unit,
            LocalDate effectiveDate,
            LocalDate expirationDate,
            String sourceUrl,
            LocalDate lastVerifiedAt,
            String verifiedBy,
            @NotBlank String verificationStatus,
            String notes
    ) {}

    public record EquivalencyRequest(
            @NotBlank String sourceType,
            @NotBlank String sourceIdentifier,
            UUID institutionId,
            UUID programId,
            @NotBlank String catalogYear,
            UUID requirementSlotId,
            @NotBlank String status,
            String sourceUrl,
            LocalDate lastVerifiedAt,
            String verifiedBy,
            @NotBlank String verificationStatus
    ) {}
}
