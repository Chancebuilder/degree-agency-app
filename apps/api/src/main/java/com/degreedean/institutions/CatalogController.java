package com.degreedean.institutions;

import com.degreedean.common.ApiException;
import com.degreedean.creditcatalog.CreditOpportunity;
import com.degreedean.creditcatalog.CreditProvider;
import com.degreedean.policies.PolicyRule;
import com.degreedean.policies.PolicyRuleRepository;
import com.degreedean.programs.Program;
import com.degreedean.programs.RequirementSlot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CatalogController {
    private final InstitutionRepository institutions;
    private final PolicyRuleRepository policies;
    @PersistenceContext
    private EntityManager entityManager;

    public CatalogController(InstitutionRepository institutions, PolicyRuleRepository policies) {
        this.institutions = institutions;
        this.policies = policies;
    }

    @GetMapping("/institutions")
    public List<Map<String, Object>> institutions() {
        return institutions.findAll().stream().map(this::toInstitution).toList();
    }

    @GetMapping("/institutions/{id}")
    public Map<String, Object> institution(@PathVariable UUID id) {
        Institution institution = institutions.findById(id).orElseThrow(() -> ApiException.notFound("Institution not found"));
        List<Program> programs = entityManager.createQuery(
                        "select p from Program p where p.institutionId = :id", Program.class)
                .setParameter("id", id)
                .getResultList();
        return Map.of("institution", toInstitution(institution), "programs", programs);
    }

    @GetMapping("/programs")
    public List<Program> programs() {
        return entityManager.createQuery("select p from Program p", Program.class).getResultList();
    }

    @GetMapping("/programs/{id}")
    public Map<String, Object> program(@PathVariable UUID id) {
        Program program = entityManager.find(Program.class, id);
        if (program == null) {
            throw ApiException.notFound("Program not found");
        }
        Institution institution = institutions.findById(program.getInstitutionId())
                .orElseThrow(() -> ApiException.notFound("Institution not found"));
        List<RequirementSlot> slots = entityManager.createQuery(
                        "select s from RequirementSlot s where s.programId = :id order by s.sortOrder", RequirementSlot.class)
                .setParameter("id", id)
                .getResultList();
        List<PolicyRule> rules = policies.findByProgramId(id);
        return Map.of(
                "program", program,
                "institution", toInstitution(institution),
                "slots", slots,
                "policies", rules
        );
    }

    @GetMapping("/providers")
    public List<CreditProvider> providers() {
        return entityManager.createQuery("select p from CreditProvider p", CreditProvider.class).getResultList();
    }

    @GetMapping("/opportunities")
    public List<CreditOpportunity> opportunities() {
        return entityManager.createQuery("select o from CreditOpportunity o", CreditOpportunity.class).getResultList();
    }

    private Map<String, Object> toInstitution(Institution institution) {
        boolean supported = "A".equals(institution.getTier());
        Map<String, Object> view = new java.util.LinkedHashMap<>();
        view.put("id", institution.getId());
        view.put("code", institution.getCode());
        view.put("name", institution.getName());
        view.put("shortName", institution.getShortName());
        view.put("tier", institution.getTier());
        view.put("deliveryModel", institution.getDeliveryModel());
        view.put("supported", supported);
        view.put("sourceUrl", institution.getSourceUrl());
        view.put("lastVerifiedAt", institution.getLastVerifiedAt());
        view.put("verificationStatus", institution.getVerificationStatus());
        view.put("notes", institution.getNotes() == null ? "" : institution.getNotes());
        return view;
    }
}
