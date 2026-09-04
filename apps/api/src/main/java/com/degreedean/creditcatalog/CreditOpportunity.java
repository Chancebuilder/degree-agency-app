package com.degreedean.creditcatalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_opportunities")
public class CreditOpportunity {
    @Id
    private UUID id;
    @Column(name = "provider_id", nullable = false)
    private UUID providerId;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String title;
    @Column(name = "opportunity_type", nullable = false)
    private String opportunityType;
    @Column(name = "ace_id")
    private String aceId;
    @Column(name = "nccrs_id")
    private String nccrsId;
    @Column(nullable = false)
    private int credits;
    @Column(nullable = false)
    private String level;
    @Column(name = "hours_per_credit")
    private BigDecimal hoursPerCredit;
    @Column(name = "effort_hours_override")
    private BigDecimal effortHoursOverride;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "exam_fee", nullable = false)
    private BigDecimal examFee;
    @Column(name = "recommendation_start")
    private LocalDate recommendationStart;
    @Column(name = "recommendation_expires")
    private LocalDate recommendationExpires;
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
    public UUID getProviderId() { return providerId; }
    public void setProviderId(UUID providerId) { this.providerId = providerId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOpportunityType() { return opportunityType; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }
    public String getAceId() { return aceId; }
    public void setAceId(String aceId) { this.aceId = aceId; }
    public String getNccrsId() { return nccrsId; }
    public void setNccrsId(String nccrsId) { this.nccrsId = nccrsId; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public BigDecimal getHoursPerCredit() { return hoursPerCredit; }
    public void setHoursPerCredit(BigDecimal hoursPerCredit) { this.hoursPerCredit = hoursPerCredit; }
    public BigDecimal getEffortHoursOverride() { return effortHoursOverride; }
    public void setEffortHoursOverride(BigDecimal effortHoursOverride) { this.effortHoursOverride = effortHoursOverride; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getExamFee() { return examFee; }
    public void setExamFee(BigDecimal examFee) { this.examFee = examFee; }
    public LocalDate getRecommendationStart() { return recommendationStart; }
    public void setRecommendationStart(LocalDate recommendationStart) { this.recommendationStart = recommendationStart; }
    public LocalDate getRecommendationExpires() { return recommendationExpires; }
    public void setRecommendationExpires(LocalDate recommendationExpires) { this.recommendationExpires = recommendationExpires; }
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
