package com.degreedean.policies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "policy_rules")
public class PolicyRule {
    @Id
    private UUID id;
    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;
    @Column(name = "program_id")
    private UUID programId;
    @Column(name = "catalog_year", nullable = false)
    private String catalogYear;
    @Column(name = "rule_type", nullable = false)
    private String ruleType;
    @Column(name = "rule_value", nullable = false)
    private String ruleValue;
    @Column(nullable = false)
    private String unit;
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "last_verified_at")
    private LocalDate lastVerifiedAt;
    @Column(name = "verified_by")
    private String verifiedBy;
    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;
    private String notes;
    private int version = 1;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getInstitutionId() { return institutionId; }
    public void setInstitutionId(UUID institutionId) { this.institutionId = institutionId; }
    public UUID getProgramId() { return programId; }
    public void setProgramId(UUID programId) { this.programId = programId; }
    public String getCatalogYear() { return catalogYear; }
    public void setCatalogYear(String catalogYear) { this.catalogYear = catalogYear; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public String getRuleValue() { return ruleValue; }
    public void setRuleValue(String ruleValue) { this.ruleValue = ruleValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public LocalDate getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDate lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
