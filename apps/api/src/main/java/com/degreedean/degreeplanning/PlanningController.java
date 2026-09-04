package com.degreedean.degreeplanning;

import com.degreedean.common.ApiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PlanningController {
    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @PostMapping("/goals")
    public Map<String, Object> createGoal(Authentication auth, @Valid @RequestBody GoalRequest request) {
        return planningService.createGoal(userId(auth), request.degreeFamily(), request.institutionId(), request.programId());
    }

    @GetMapping("/goals")
    public List<DegreeGoal> goals(Authentication auth) {
        return planningService.listGoals(userId(auth));
    }

    @GetMapping("/plans")
    public List<DegreePlan> plans(Authentication auth) {
        return planningService.listPlans(userId(auth));
    }

    @PostMapping("/plans/{planId}/match")
    public Map<String, Object> match(Authentication auth, @PathVariable UUID planId) {
        return planningService.runMatch(userId(auth), planId);
    }

    @GetMapping("/plans/{planId}/match")
    public Map<String, Object> getMatch(Authentication auth, @PathVariable UUID planId) {
        return planningService.getMatch(userId(auth), planId);
    }

    @PostMapping("/plans/{planId}/simulate")
    public Map<String, Object> simulate(Authentication auth, @PathVariable UUID planId) {
        return planningService.simulate(userId(auth), planId);
    }

    @GetMapping("/plans/{planId}/scenarios")
    public Map<String, Object> scenarios(Authentication auth, @PathVariable UUID planId) {
        return planningService.simulate(userId(auth), planId);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication auth) {
        return planningService.dashboard(userId(auth));
    }

    private static UUID userId(Authentication auth) {
        if (auth == null) {
            throw ApiException.unauthorized("Sign in required");
        }
        return UUID.fromString(auth.getName());
    }

    public record GoalRequest(
            @NotBlank String degreeFamily,
            @NotNull UUID institutionId,
            @NotNull UUID programId
    ) {}
}
