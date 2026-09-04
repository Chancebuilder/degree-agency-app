package com.degreedean.consent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consent_records")
public class ConsentRecord {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String purpose;
    @Column(nullable = false)
    private boolean granted;
    @Column(name = "policy_version", nullable = false)
    private String policyVersion;
    @Column(name = "acknowledgment_text")
    private String acknowledgmentText;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public boolean isGranted() { return granted; }
    public void setGranted(boolean granted) { this.granted = granted; }
    public String getPolicyVersion() { return policyVersion; }
    public void setPolicyVersion(String policyVersion) { this.policyVersion = policyVersion; }
    public String getAcknowledgmentText() { return acknowledgmentText; }
    public void setAcknowledgmentText(String acknowledgmentText) { this.acknowledgmentText = acknowledgmentText; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getWithdrawnAt() { return withdrawnAt; }
    public void setWithdrawnAt(Instant withdrawnAt) { this.withdrawnAt = withdrawnAt; }
}
