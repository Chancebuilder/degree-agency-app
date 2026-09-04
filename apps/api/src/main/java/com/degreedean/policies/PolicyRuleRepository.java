package com.degreedean.policies;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRuleRepository extends JpaRepository<PolicyRule, UUID> {
    List<PolicyRule> findByProgramId(UUID programId);
    Optional<PolicyRule> findByProgramIdAndRuleType(UUID programId, String ruleType);
}
