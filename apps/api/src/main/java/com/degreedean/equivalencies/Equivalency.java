package com.degreedean.equivalencies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equivalencies")
public class Equivalency {
    @Id
    private UUID id;
    @Column(name = "source_type", nullable = false)
    private String sourceType;
    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;
    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;
    @Column(name = "program_id", nullable = false)
    private UUID programId;
    @Column(name = "catalog_year", nullable = false)
    private String catalogYear;
    @Column(name = "requirement_slot_id", nullable = false)
    private UUID requirementSlotId;
    @Column(nullable = false)
    private String status;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "last_verified_at")
    private LocalDate lastVerifiedAt;
    @Column(name = "verified_by")
    private String verifiedBy;
    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;
    private int version = 1;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public UUID getInstitutionId() { return institutionId; }
    public void setInstitutionId(UUID institutionId) { this.institutionId = institutionId; }
    public UUID getProgramId() { return programId; }
    public void setProgramId(UUID programId) { this.programId = programId; }
    public String getCatalogYear() { return catalogYear; }
    public void setCatalogYear(String catalogYear) { this.catalogYear = catalogYear; }
    public UUID getRequirementSlotId() { return requirementSlotId; }
    public void setRequirementSlotId(UUID requirementSlotId) { this.requirementSlotId = requirementSlotId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public LocalDate getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDate lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
