package com.degreedean.priorlearning;

import java.util.UUID;

/**
 * Class 2 stub. PLA / portfolio ROI is schema and interface only.
 */
public interface PriorLearningPort {
    PlaStub createStub(UUID userId, String title);

    record PlaStub(UUID id, String title, String status) {}
}
