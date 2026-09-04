package com.degreedean.equivalencies;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquivalencyRepository extends JpaRepository<Equivalency, UUID> {
    List<Equivalency> findByProgramIdAndCatalogYear(UUID programId, String catalogYear);
    List<Equivalency> findBySourceTypeAndSourceIdentifierAndProgramIdAndCatalogYear(
            String sourceType, String sourceIdentifier, UUID programId, String catalogYear);
}
