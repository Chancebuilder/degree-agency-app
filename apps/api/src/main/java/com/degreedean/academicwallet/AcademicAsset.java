package com.degreedean.academicwallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "academic_assets")
public class AcademicAsset {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "source_type", nullable = false)
    private String sourceType;
    @Column(name = "source_identifier")
    private String sourceIdentifier;
    @Column(name = "institution_name")
    private String institutionName;
    @Column(name = "course_code")
    private String courseCode;
    @Column(name = "course_title", nullable = false)
    private String courseTitle;
    @Column(nullable = false)
    private BigDecimal credits;
    private String grade;
    private String term;
    @Column(name = "completion_date")
    private LocalDate completionDate;
    @Column(name = "transcript_origin")
    private String transcriptOrigin;
    @Column(name = "original_learning_source")
    private String originalLearningSource;
    private String evaluator;
    @Column(name = "ace_id")
    private String aceId;
    @Column(name = "nccrs_id")
    private String nccrsId;
    @Column(name = "verified_by_student", nullable = false)
    private boolean verifiedByStudent;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
    public String getTranscriptOrigin() { return transcriptOrigin; }
    public void setTranscriptOrigin(String transcriptOrigin) { this.transcriptOrigin = transcriptOrigin; }
    public String getOriginalLearningSource() { return originalLearningSource; }
    public void setOriginalLearningSource(String originalLearningSource) { this.originalLearningSource = originalLearningSource; }
    public String getEvaluator() { return evaluator; }
    public void setEvaluator(String evaluator) { this.evaluator = evaluator; }
    public String getAceId() { return aceId; }
    public void setAceId(String aceId) { this.aceId = aceId; }
    public String getNccrsId() { return nccrsId; }
    public void setNccrsId(String nccrsId) { this.nccrsId = nccrsId; }
    public boolean isVerifiedByStudent() { return verifiedByStudent; }
    public void setVerifiedByStudent(boolean verifiedByStudent) { this.verifiedByStudent = verifiedByStudent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
