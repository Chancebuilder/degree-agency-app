package com.degreedean.studentprofile;

import com.degreedean.academicwallet.AcademicAsset;
import com.degreedean.academicwallet.AcademicAssetRepository;
import com.degreedean.common.ApiException;
import com.degreedean.config.AppProperties;
import com.degreedean.consent.ConsentRecord;
import com.degreedean.consent.ConsentRecordRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class StudentFoundationController {
    private static final Set<String> REQUIRED = Set.of(
            "DEGREE_PLANNING",
            "ACCOUNT_STORAGE",
            "DISCLOSURE_ACKNOWLEDGMENT"
    );
    private static final Set<String> OPTIONAL = Set.of(
            "ADVISOR_ACCESS",
            "MARKETING",
            "INSTITUTION_MATCHING",
            "INSTITUTION_PROFILE_SHARING",
            "TRANSCRIPT_SHARING"
    );

    private final StudentProfileRepository profiles;
    private final ConsentRecordRepository consents;
    private final AcademicAssetRepository assets;
    private final AppProperties properties;

    public StudentFoundationController(
            StudentProfileRepository profiles,
            ConsentRecordRepository consents,
            AcademicAssetRepository assets,
            AppProperties properties
    ) {
        this.profiles = profiles;
        this.consents = consents;
        this.assets = assets;
        this.properties = properties;
    }

    @PostMapping("/onboarding/age")
    @Transactional
    public Map<String, Object> confirmAge(Authentication auth, @Valid @RequestBody AgeRequest request) {
        UUID userId = userId(auth);
        if (request.dateOfBirth() == null) {
            throw ApiException.badRequest("Date of birth is required");
        }
        int age = Period.between(request.dateOfBirth(), LocalDate.now()).getYears();
        if (age < 18) {
            StudentProfile profile = profiles.findByUserId(userId).orElseGet(() -> newProfile(userId));
            profile.setDateOfBirth(request.dateOfBirth());
            profile.setAgeConfirmed(false);
            profile.setFirstName(profile.getFirstName() == null ? "Pending" : profile.getFirstName());
            profile.setLastName(profile.getLastName() == null ? "Eligibility" : profile.getLastName());
            persistProfile(profile);
            return Map.of(
                    "eligible", false,
                    "message", "Degree Dean is for adults 18 and older. A future guardian-consent pathway is documented for Phase 2. Transcript collection is not available."
            );
        }
        StudentProfile profile = profiles.findByUserId(userId).orElseGet(() -> newProfile(userId));
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setAgeConfirmed(true);
        profile.setAgeConfirmedAt(Instant.now());
        persistProfile(profile);
        return Map.of("eligible", true, "age", age);
    }

    @PostMapping("/consents")
    @Transactional
    public List<ConsentRecord> submitConsents(Authentication auth, @Valid @RequestBody ConsentBatchRequest request) {
        UUID userId = userId(auth);
        for (String required : REQUIRED) {
            boolean granted = request.items().stream()
                    .anyMatch(item -> required.equals(item.purpose()) && item.granted());
            if (!granted) {
                throw ApiException.badRequest("Required consent is missing: " + required);
            }
        }
        for (ConsentItem item : request.items()) {
            if (!REQUIRED.contains(item.purpose()) && !OPTIONAL.contains(item.purpose())) {
                throw ApiException.badRequest("Unknown consent purpose: " + item.purpose());
            }
            ConsentRecord record = new ConsentRecord();
            record.setId(UUID.randomUUID());
            record.setUserId(userId);
            record.setPurpose(item.purpose());
            record.setGranted(item.granted());
            record.setPolicyVersion(properties.getDisclosureVersion());
            record.setAcknowledgmentText(item.acknowledgmentText());
            record.setCreatedAt(Instant.now());
            consents.save(record);
        }
        return consents.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/consents")
    public List<ConsentRecord> listConsents(Authentication auth) {
        return consents.findByUserIdOrderByCreatedAtDesc(userId(auth));
    }

    @PostMapping("/consents/{purpose}/withdraw")
    @Transactional
    public ConsentRecord withdraw(Authentication auth, @PathVariable String purpose) {
        if (REQUIRED.contains(purpose)) {
            throw ApiException.badRequest("Required consents cannot be withdrawn while the account remains active. Export or delete the account instead.");
        }
        ConsentRecord record = new ConsentRecord();
        record.setId(UUID.randomUUID());
        record.setUserId(userId(auth));
        record.setPurpose(purpose);
        record.setGranted(false);
        record.setPolicyVersion(properties.getDisclosureVersion());
        record.setCreatedAt(Instant.now());
        record.setWithdrawnAt(Instant.now());
        return consents.save(record);
    }

    @PutMapping("/profile")
    @Transactional
    public StudentProfile upsertProfile(Authentication auth, @Valid @RequestBody ProfileRequest request) {
        UUID userId = userId(auth);
        requireEligible(userId);
        StudentProfile profile = profiles.findByUserId(userId).orElseGet(() -> newProfile(userId));
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        if (request.weeklyStudyHours() != null) {
            profile.setWeeklyStudyHours(request.weeklyStudyHours());
        }
        profile.setMonthlyBudget(request.monthlyBudget());
        if (request.intensity() != null) {
            profile.setIntensity(request.intensity());
        }
        if (request.maxConcurrent() != null) {
            profile.setMaxConcurrent(Math.max(1, Math.min(4, request.maxConcurrent())));
        }
        if (request.competencyWilling() != null) {
            profile.setCompetencyWilling(request.competencyWilling());
        }
        if (request.plaWilling() != null) {
            profile.setPlaWilling(request.plaWilling());
        }
        if (request.examWilling() != null) {
            profile.setExamWilling(request.examWilling());
        }
        profile.setTargetGraduationDate(request.targetGraduationDate());
        profile.setStartDate(request.startDate() == null ? LocalDate.now() : request.startDate());
        persistProfile(profile);
        return profile;
    }

    @GetMapping("/profile")
    public StudentProfile getProfile(Authentication auth) {
        return profiles.findByUserId(userId(auth)).orElseThrow(() -> ApiException.notFound("Profile not created yet"));
    }

    @GetMapping("/wallet")
    public List<AcademicAsset> wallet(Authentication auth) {
        return assets.findByUserIdOrderByCreatedAtAsc(userId(auth));
    }

    @PostMapping("/wallet/assets")
    @Transactional
    public List<AcademicAsset> saveAssets(Authentication auth, @Valid @RequestBody WalletBatchRequest request) {
        UUID userId = userId(auth);
        requireEligible(userId);
        requireConsent(userId, "DEGREE_PLANNING");
        if (request.assets() == null || request.assets().isEmpty()) {
            throw ApiException.badRequest("Submit at least one reviewed academic asset");
        }
        long existing = assets.countByUserId(userId);
        if (existing + request.assets().size() > 30) {
            throw ApiException.forbidden("Free tier allows 30 Academic Wallet assets. Upgrade is not implemented in MVP checkout.");
        }
        Instant now = Instant.now();
        return request.assets().stream().map(item -> {
            validateAsset(item);
            AcademicAsset asset = new AcademicAsset();
            asset.setId(UUID.randomUUID());
            asset.setUserId(userId);
            asset.setSourceType(item.sourceType());
            asset.setSourceIdentifier(first(item.sourceIdentifier(), item.courseCode()));
            asset.setInstitutionName(item.institutionName());
            asset.setCourseCode(item.courseCode());
            asset.setCourseTitle(item.courseTitle());
            asset.setCredits(item.credits());
            asset.setGrade(item.grade());
            asset.setTerm(item.term());
            asset.setCompletionDate(item.completionDate());
            asset.setTranscriptOrigin(item.transcriptOrigin());
            asset.setOriginalLearningSource(item.originalLearningSource());
            asset.setEvaluator(item.evaluator());
            asset.setAceId(item.aceId());
            asset.setNccrsId(item.nccrsId());
            asset.setVerifiedByStudent(Boolean.TRUE.equals(item.verifiedByStudent()));
            if (!asset.isVerifiedByStudent()) {
                throw ApiException.badRequest("Every asset must be reviewed and verified by the student before it is saved");
            }
            asset.setCreatedAt(now);
            asset.setUpdatedAt(now);
            return assets.save(asset);
        }).toList();
    }

    private void validateAsset(AssetRequest item) {
        if (item.courseTitle() == null || item.courseTitle().isBlank()) {
            throw ApiException.badRequest("Course title is required");
        }
        if (item.credits() == null || item.credits().signum() <= 0 || item.credits().compareTo(BigDecimal.valueOf(20)) > 0) {
            throw ApiException.badRequest("Credits must be between 0.5 and 20");
        }
        Set<String> allowed = Set.of(
                "INSTITUTION_COURSE", "PROVIDER_COURSE", "EXAM", "CERTIFICATION",
                "MILITARY", "EMPLOYER_TRAINING", "PLA"
        );
        if (!allowed.contains(item.sourceType())) {
            throw ApiException.badRequest("Unsupported source type");
        }
    }

    private void requireEligible(UUID userId) {
        StudentProfile profile = profiles.findByUserId(userId)
                .orElseThrow(() -> ApiException.forbidden("Confirm that you are 18 or older before continuing"));
        if (!profile.isAgeConfirmed()) {
            throw ApiException.forbidden("Degree Dean is for adults 18 and older");
        }
    }

    private void requireConsent(UUID userId, String purpose) {
        boolean granted = consents.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(c -> purpose.equals(c.getPurpose()))
                .findFirst()
                .map(ConsentRecord::isGranted)
                .orElse(false);
        if (!granted) {
            throw ApiException.forbidden("Required consent is missing: " + purpose);
        }
    }

    private StudentProfile newProfile(UUID userId) {
        StudentProfile profile = new StudentProfile();
        profile.setId(UUID.randomUUID());
        profile.setUserId(userId);
        profile.setFirstName("Student");
        profile.setLastName("Learner");
        return profile;
    }

    private void persistProfile(StudentProfile profile) {
        Instant now = Instant.now();
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(now);
        }
        profile.setUpdatedAt(now);
        profiles.save(profile);
    }

    private static UUID userId(Authentication auth) {
        return UUID.fromString(auth.getName());
    }

    private static String first(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b;
    }

    public record AgeRequest(LocalDate dateOfBirth) {}
    public record ConsentItem(@NotBlank String purpose, boolean granted, String acknowledgmentText) {}
    public record ConsentBatchRequest(List<ConsentItem> items) {}
    public record ProfileRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            Integer weeklyStudyHours,
            BigDecimal monthlyBudget,
            String intensity,
            Integer maxConcurrent,
            Boolean competencyWilling,
            Boolean plaWilling,
            Boolean examWilling,
            LocalDate targetGraduationDate,
            LocalDate startDate
    ) {}
    public record WalletBatchRequest(List<AssetRequest> assets) {}
    public record AssetRequest(
            String sourceType,
            String sourceIdentifier,
            String institutionName,
            String courseCode,
            String courseTitle,
            BigDecimal credits,
            String grade,
            String term,
            LocalDate completionDate,
            String transcriptOrigin,
            String originalLearningSource,
            String evaluator,
            String aceId,
            String nccrsId,
            Boolean verifiedByStudent
    ) {}
}
