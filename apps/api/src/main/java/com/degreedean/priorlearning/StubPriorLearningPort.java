package com.degreedean.priorlearning;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StubPriorLearningPort implements PriorLearningPort {
    private final PlaAssessmentRepository repository;

    public StubPriorLearningPort(PlaAssessmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlaStub createStub(UUID userId, String title) {
        PlaAssessment assessment = new PlaAssessment();
        assessment.setId(UUID.randomUUID());
        assessment.setUserId(userId);
        assessment.setTitle(title);
        assessment.setStatus("STUB");
        assessment.setCreatedAt(Instant.now());
        repository.save(assessment);
        return new PlaStub(assessment.getId(), title, "STUB");
    }
}

@Entity
@Table(name = "pla_assessments")
class PlaAssessment {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String title;
    @Column(name = "estimated_credits")
    private Integer estimatedCredits;
    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;
    @Column(name = "estimated_hours")
    private BigDecimal estimatedHours;
    @Column(nullable = false)
    private String status = "STUB";
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getEstimatedCredits() { return estimatedCredits; }
    public void setEstimatedCredits(Integer estimatedCredits) { this.estimatedCredits = estimatedCredits; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

interface PlaAssessmentRepository extends JpaRepository<PlaAssessment, UUID> {}
