package com.scholarship.finder.service;

import com.scholarship.finder.dto.SchemeDto;
import com.scholarship.finder.dto.SchemeMatchResultDto;
import com.scholarship.finder.dto.StudentProfileDto;
import com.scholarship.finder.entity.Scheme;
import com.scholarship.finder.entity.SchemeEligibilityRule;
import com.scholarship.finder.repository.SchemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EligibilityEngineService {

    private final SchemeRepository schemeRepository;
    private final SchemeService schemeService;

    public EligibilityEngineService(SchemeRepository schemeRepository, SchemeService schemeService) {
        this.schemeRepository = schemeRepository;
        this.schemeService = schemeService;
    }

    /**
     * Evaluates all schemes against the student profile.
     * 1. Filters schemes matching the student's education level.
     * 2. Evaluates eligibility rules and computes match percentage and explanations.
     * 3. Returns sorted list by match score descending.
     */
    public List<SchemeMatchResultDto> evaluateEligibility(StudentProfileDto profile) {
        if (profile == null || profile.getEducationLevel() == null) {
            return Collections.emptyList();
        }

        // 1. Filter by education level first
        List<Scheme> candidateSchemes = schemeRepository.findByEducationLevel(profile.getEducationLevel().trim());

        List<SchemeMatchResultDto> results = new ArrayList<>();
        for (Scheme scheme : candidateSchemes) {
            results.add(evaluateScheme(scheme, profile));
        }

        // Sort by match score descending, then by benefit amount descending
        results.sort((a, b) -> {
            int scoreComp = Double.compare(b.getMatchScore(), a.getMatchScore());
            if (scoreComp != 0) return scoreComp;
            Double amountA = a.getScheme().getBenefitAmount() != null ? a.getScheme().getBenefitAmount() : 0.0;
            Double amountB = b.getScheme().getBenefitAmount() != null ? b.getScheme().getBenefitAmount() : 0.0;
            return Double.compare(amountB, amountA);
        });

        return results;
    }

    /**
     * Evaluates a single scheme against the profile.
     */
    public SchemeMatchResultDto evaluateScheme(Scheme scheme, StudentProfileDto profile) {
        SchemeDto schemeDto = schemeService.toDto(scheme);
        SchemeEligibilityRule rule = scheme.getEligibilityRule();

        List<String> matched = new ArrayList<>();
        List<String> unmet = new ArrayList<>();
        int activeRules = 0;
        int satisfiedRules = 0;

        // Base level match check
        matched.add("Matches your current education level: " + profile.getEducationLevel());

        if (rule == null) {
            // Open to all students at this education level
            double score = 100.0;
            return new SchemeMatchResultDto(schemeDto, score, true, 1, 1,
                    List.of("✓ Open to all students enrolled in " + profile.getEducationLevel()),
                    List.of("General eligibility open to all"),
                    Collections.emptyList());
        }

        // 1. Age Rule
        int studentAge = calculateAge(profile.getDateOfBirth());
        if (rule.getMinAge() != null || rule.getMaxAge() != null) {
            activeRules++;
            boolean ageOk = true;
            if (rule.getMinAge() != null && studentAge < rule.getMinAge()) ageOk = false;
            if (rule.getMaxAge() != null && studentAge > rule.getMaxAge()) ageOk = false;

            if (ageOk) {
                satisfiedRules++;
                matched.add(String.format("Age (%d years) satisfies age requirement [%s - %s years]",
                        studentAge,
                        rule.getMinAge() != null ? rule.getMinAge() : "Any",
                        rule.getMaxAge() != null ? rule.getMaxAge() : "Any"));
            } else {
                unmet.add(String.format("Age (%d years) does not meet requirement [%s - %s years]",
                        studentAge,
                        rule.getMinAge() != null ? rule.getMinAge() : "Any",
                        rule.getMaxAge() != null ? rule.getMaxAge() : "Any"));
            }
        }

        // 2. Family Income Rule
        if (rule.getMaxIncome() != null) {
            activeRules++;
            Double studentIncome = profile.getAnnualFamilyIncome();
            if (studentIncome != null && studentIncome <= rule.getMaxIncome()) {
                satisfiedRules++;
                matched.add(String.format("Annual family income (₹%,.0f) is within limit of ₹%,.0f",
                        studentIncome, rule.getMaxIncome()));
            } else {
                unmet.add(String.format("Annual family income (₹%,.0f) exceeds ceiling limit of ₹%,.0f",
                        studentIncome != null ? studentIncome : 0.0, rule.getMaxIncome()));
            }
        }

        // 3. Gender Rule
        if (rule.getGender() != null && !rule.getGender().isBlank()) {
            activeRules++;
            if (profile.getGender() != null && profile.getGender().equalsIgnoreCase(rule.getGender().trim())) {
                satisfiedRules++;
                matched.add("Gender (" + profile.getGender() + ") meets scheme requirement");
            } else {
                unmet.add("Scheme is exclusively reserved for " + rule.getGender() + " applicants");
            }
        }

        // 4. Community Category Rule
        if (rule.getCommunityCategory() != null && !rule.getCommunityCategory().isBlank()) {
            activeRules++;
            List<String> allowedCommunities = Arrays.stream(rule.getCommunityCategory().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            String studentCat = profile.getCommunityCategory() != null ? profile.getCommunityCategory().trim().toUpperCase() : "";
            if (allowedCommunities.contains(studentCat)) {
                satisfiedRules++;
                matched.add("Community category (" + profile.getCommunityCategory() + ") is eligible");
            } else {
                unmet.add("Reserved for " + rule.getCommunityCategory() + " categories (Your category: " +
                        (profile.getCommunityCategory() != null ? profile.getCommunityCategory() : "Not specified") + ")");
            }
        }

        // 5. Religious Minority Requirement
        if (Boolean.TRUE.equals(rule.getMinorityRequired())) {
            activeRules++;
            boolean isMinority = Boolean.TRUE.equals(profile.getReligionMinorityFlag()) ||
                    "Minority".equalsIgnoreCase(profile.getCommunityCategory());
            if (isMinority) {
                satisfiedRules++;
                matched.add("Notified Religious Minority status verified");
            } else {
                unmet.add("Applicant must belong to a notified religious minority community");
            }
        }

        // 6. Minimum Exam Percentage
        if (rule.getMinPercentage() != null) {
            activeRules++;
            Double studentScore = profile.getPreviousExamPercentage();
            if (studentScore != null && studentScore >= rule.getMinPercentage()) {
                satisfiedRules++;
                matched.add(String.format("Academic score (%.1f%%) meets or exceeds cutoff (%.1f%%)",
                        studentScore, rule.getMinPercentage()));
            } else {
                unmet.add(String.format("Academic score (%.1f%%) is below required cutoff of %.1f%%",
                        studentScore != null ? studentScore : 0.0, rule.getMinPercentage()));
            }
        }

        // 7. Institution Type (e.g. Government, Aided)
        if (rule.getInstitutionType() != null && !rule.getInstitutionType().isBlank()) {
            activeRules++;
            List<String> allowedTypes = Arrays.stream(rule.getInstitutionType().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            String studentInstType = profile.getInstitutionType() != null ? profile.getInstitutionType().trim().toUpperCase() : "";
            if (allowedTypes.contains(studentInstType)) {
                satisfiedRules++;
                matched.add("Institution type (" + profile.getInstitutionType() + ") is eligible");
            } else {
                unmet.add("Restricted to students in " + rule.getInstitutionType() + " institutions");
            }
        }

        // 8. State / Domicile
        if (rule.getState() != null && !rule.getState().isBlank()) {
            activeRules++;
            if (profile.getState() != null && profile.getState().equalsIgnoreCase(rule.getState().trim())) {
                satisfiedRules++;
                matched.add("Domicile / State (" + profile.getState() + ") matches requirement");
            } else {
                unmet.add("Scheme is restricted to residents/students of " + rule.getState());
            }
        }

        // 9. Course / Stream
        if (rule.getCourseStream() != null && !rule.getCourseStream().isBlank()) {
            activeRules++;
            List<String> allowedStreams = Arrays.stream(rule.getCourseStream().split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            String studentStream = (profile.getStream() != null ? profile.getStream() : "") + " " +
                                   (profile.getCourse() != null ? profile.getCourse() : "");
            studentStream = studentStream.toLowerCase();

            boolean streamMatched = false;
            for (String allowed : allowedStreams) {
                if (studentStream.contains(allowed)) {
                    streamMatched = true;
                    break;
                }
            }

            if (streamMatched) {
                satisfiedRules++;
                matched.add("Course/Stream matches allowed disciplines (" + rule.getCourseStream() + ")");
            } else {
                unmet.add("Eligible only for disciplines: " + rule.getCourseStream());
            }
        }

        // 10. Disability Status
        if (Boolean.TRUE.equals(rule.getDisabilityRequired())) {
            activeRules++;
            if (Boolean.TRUE.equals(profile.getDisabilityStatus())) {
                satisfiedRules++;
                matched.add("Specially-Abled / Differently-Abled status verified");
            } else {
                unmet.add("Scheme is exclusively designed for students with benchmark disability (>=40%)");
            }
        }

        // 11. Single Girl Child
        if (Boolean.TRUE.equals(rule.getSingleGirlChildRequired())) {
            activeRules++;
            if (Boolean.TRUE.equals(profile.getSingleGirlChild())) {
                satisfiedRules++;
                matched.add("Single Girl Child status confirmed");
            } else {
                unmet.add("Exclusively for families with an only single girl child");
            }
        }

        // 12. First Graduate
        if (Boolean.TRUE.equals(rule.getFirstGraduateRequired())) {
            activeRules++;
            if (Boolean.TRUE.equals(profile.getFirstGraduate())) {
                satisfiedRules++;
                matched.add("First Graduate in the family status verified");
            } else {
                unmet.add("Requires First Graduate certificate from Tahsildar");
            }
        }

        // 13. Govt School Only
        if (Boolean.TRUE.equals(rule.getGovtSchoolOnly())) {
            activeRules++;
            boolean isGovt = "Government".equalsIgnoreCase(profile.getInstitutionType());
            if (isGovt) {
                satisfiedRules++;
                matched.add("Studied in Government Institution (qualifies for govt school benefit)");
            } else {
                unmet.add("Mandatory requirement: Must have studied in Government School/College");
            }
        }

        double score = 100.0;
        if (activeRules > 0) {
            score = Math.round(((double) satisfiedRules / activeRules) * 1000.0) / 10.0;
        }

        boolean eligible = unmet.isEmpty();

        List<String> reasons = new ArrayList<>();
        for (String m : matched) {
            reasons.add("✓ " + m);
        }
        for (String u : unmet) {
            reasons.add("✗ " + u);
        }

        return new SchemeMatchResultDto(schemeDto, score, eligible, satisfiedRules, activeRules,
                reasons, matched, unmet);
    }

    private int calculateAge(LocalDate dob) {
        if (dob == null) {
            return 18; // reasonable default
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }
}

