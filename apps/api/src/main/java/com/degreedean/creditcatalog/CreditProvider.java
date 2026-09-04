package com.degreedean.creditcatalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_providers")
public class CreditProvider {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(name = "delivery_platform", nullable = false)
    private String deliveryPlatform;
    @Column(nullable = false)
    private String evaluator;
    @Column(name = "default_hours_per_credit", nullable = false)
    private BigDecimal defaultHoursPerCredit;
    @Column(name = "pricing_model", nullable = false)
    private String pricingModel;
    @Column(name = "monthly_price", nullable = false)
    private BigDecimal monthlyPrice;
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
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDeliveryPlatform() { return deliveryPlatform; }
    public void setDeliveryPlatform(String deliveryPlatform) { this.deliveryPlatform = deliveryPlatform; }
    public String getEvaluator() { return evaluator; }
    public void setEvaluator(String evaluator) { this.evaluator = evaluator; }
    public BigDecimal getDefaultHoursPerCredit() { return defaultHoursPerCredit; }
    public void setDefaultHoursPerCredit(BigDecimal defaultHoursPerCredit) { this.defaultHoursPerCredit = defaultHoursPerCredit; }
    public String getPricingModel() { return pricingModel; }
    public void setPricingModel(String pricingModel) { this.pricingModel = pricingModel; }
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
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
