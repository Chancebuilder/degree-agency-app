package com.degreedean.academicwallet;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicAssetRepository extends JpaRepository<AcademicAsset, UUID> {
    List<AcademicAsset> findByUserIdOrderByCreatedAtAsc(UUID userId);
    long countByUserId(UUID userId);
}
