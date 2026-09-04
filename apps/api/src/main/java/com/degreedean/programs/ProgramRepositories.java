package com.degreedean.programs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProgramRepository extends JpaRepository<Program, UUID> {
    List<Program> findByInstitutionId(UUID institutionId);
    Optional<Program> findByInstitutionIdAndCodeAndCatalogYear(UUID institutionId, String code, String catalogYear);
    List<Program> findByDegreeFamily(String degreeFamily);
}

interface RequirementSlotRepository extends JpaRepository<RequirementSlot, UUID> {
    List<RequirementSlot> findByProgramIdOrderBySortOrderAsc(UUID programId);
    Optional<RequirementSlot> findByProgramIdAndSlotCode(UUID programId, String slotCode);
}
