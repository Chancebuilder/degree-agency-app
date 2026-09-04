package com.degreedean.programs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "programs")
public class Program {
    @Id
    private UUID id;
    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(name = "degree_family", nullable = false)
    private String degreeFamily;
    @Column(name = "catalog_year", nullable = false)
    private String catalogYear;
    @Column(name = "total_credits", nullable = false)
    private int totalCredits;
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
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDegreeFamily() { return degreeFamily; }
    public void setDegreeFamily(String degreeFamily) { this.degreeFamily = degreeFamily; }
    public String getCatalogYear() { return catalogYear; }
    public void setCatalogYear(String catalogYear) { this.catalogYear = catalogYear; }
    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }
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
