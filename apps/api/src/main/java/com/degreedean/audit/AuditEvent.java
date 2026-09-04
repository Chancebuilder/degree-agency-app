package com.degreedean.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id
    private UUID id;
    @Column(name = "actor_id")
    private UUID actorId;
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    @Column(name = "entity_id")
    private UUID entityId;
    @Column(nullable = false)
    private String action;
    @Column(name = "before_value")
    private String beforeValue;
    @Column(name = "after_value")
    private String afterValue;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static AuditEvent of(UUID actorId, String entityType, UUID entityId, String action, String before, String after, String sourceUrl) {
        AuditEvent event = new AuditEvent();
        event.id = UUID.randomUUID();
        event.actorId = actorId;
        event.entityType = entityType;
        event.entityId = entityId;
        event.action = action;
        event.beforeValue = before;
        event.afterValue = after;
        event.sourceUrl = sourceUrl;
        event.createdAt = Instant.now();
        return event;
    }

    public UUID getId() { return id; }
    public UUID getActorId() { return actorId; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getBeforeValue() { return beforeValue; }
    public String getAfterValue() { return afterValue; }
    public String getSourceUrl() { return sourceUrl; }
    public Instant getCreatedAt() { return createdAt; }
}
