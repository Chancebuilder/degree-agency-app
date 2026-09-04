package com.degreedean.creditcatalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface CreditProviderRepository extends JpaRepository<CreditProvider, UUID> {
    Optional<CreditProvider> findByCode(String code);
}

interface CreditOpportunityRepository extends JpaRepository<CreditOpportunity, UUID> {
    Optional<CreditOpportunity> findByCode(String code);
    List<CreditOpportunity> findByProviderId(UUID providerId);
}
