package com.degreedean.studentprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "age_confirmed", nullable = false)
    private boolean ageConfirmed;
    @Column(name = "age_confirmed_at")
    private Instant ageConfirmedAt;
    @Column(name = "weekly_study_hours", nullable = false)
    private int weeklyStudyHours = 10;
    @Column(name = "monthly_budget")
    private BigDecimal monthlyBudget;
    @Column(nullable = false)
    private String intensity = "STANDARD";
    @Column(name = "max_concurrent", nullable = false)
    private int maxConcurrent = 2;
    @Column(name = "competency_willing", nullable = false)
    private boolean competencyWilling = true;
    @Column(name = "pla_willing", nullable = false)
    private boolean plaWilling;
    @Column(name = "exam_willing", nullable = false)
    private boolean examWilling = true;
    @Column(name = "target_graduation_date")
    private LocalDate targetGraduationDate;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "unavailable_weeks")
    private String unavailableWeeks;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public boolean isAgeConfirmed() { return ageConfirmed; }
    public void setAgeConfirmed(boolean ageConfirmed) { this.ageConfirmed = ageConfirmed; }
    public Instant getAgeConfirmedAt() { return ageConfirmedAt; }
    public void setAgeConfirmedAt(Instant ageConfirmedAt) { this.ageConfirmedAt = ageConfirmedAt; }
    public int getWeeklyStudyHours() { return weeklyStudyHours; }
    public void setWeeklyStudyHours(int weeklyStudyHours) { this.weeklyStudyHours = weeklyStudyHours; }
    public BigDecimal getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(BigDecimal monthlyBudget) { this.monthlyBudget = monthlyBudget; }
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public int getMaxConcurrent() { return maxConcurrent; }
    public void setMaxConcurrent(int maxConcurrent) { this.maxConcurrent = maxConcurrent; }
    public boolean isCompetencyWilling() { return competencyWilling; }
    public void setCompetencyWilling(boolean competencyWilling) { this.competencyWilling = competencyWilling; }
    public boolean isPlaWilling() { return plaWilling; }
    public void setPlaWilling(boolean plaWilling) { this.plaWilling = plaWilling; }
    public boolean isExamWilling() { return examWilling; }
    public void setExamWilling(boolean examWilling) { this.examWilling = examWilling; }
    public LocalDate getTargetGraduationDate() { return targetGraduationDate; }
    public void setTargetGraduationDate(LocalDate targetGraduationDate) { this.targetGraduationDate = targetGraduationDate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public String getUnavailableWeeks() { return unavailableWeeks; }
    public void setUnavailableWeeks(String unavailableWeeks) { this.unavailableWeeks = unavailableWeeks; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
