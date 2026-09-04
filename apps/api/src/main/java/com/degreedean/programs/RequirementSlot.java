package com.degreedean.programs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "requirement_slots")
public class RequirementSlot {
    @Id
    private UUID id;
    @Column(name = "program_id", nullable = false)
    private UUID programId;
    @Column(name = "slot_code", nullable = false)
    private String slotCode;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private int credits;
    @Column(nullable = false)
    private String level;
    @Column(name = "residency_required", nullable = false)
    private boolean residencyRequired;
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
    @Column(name = "group_name", nullable = false)
    private String groupName;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProgramId() { return programId; }
    public void setProgramId(UUID programId) { this.programId = programId; }
    public String getSlotCode() { return slotCode; }
    public void setSlotCode(String slotCode) { this.slotCode = slotCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public boolean isResidencyRequired() { return residencyRequired; }
    public void setResidencyRequired(boolean residencyRequired) { this.residencyRequired = residencyRequired; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
