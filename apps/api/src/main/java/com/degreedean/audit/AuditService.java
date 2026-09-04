package com.degreedean.audit;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    public void record(UUID actorId, String entityType, UUID entityId, String action, String before, String after, String sourceUrl) {
        repository.save(AuditEvent.of(actorId, entityType, entityId, action, before, after, sourceUrl));
    }
}
