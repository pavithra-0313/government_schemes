package com.scholarship.finder.service;

import com.scholarship.finder.dto.SchemeDto;
import com.scholarship.finder.dto.SchemeMatchResultDto;
import com.scholarship.finder.dto.StudentProfileDto;
import com.scholarship.finder.entity.Scheme;
import com.scholarship.finder.entity.SchemeEducationLevel;
import com.scholarship.finder.entity.SchemeEligibilityRule;
import com.scholarship.finder.repository.SchemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityEngineTest {

    @Mock
    private SchemeRepository schemeRepository;

    @Mock
    private SchemeService schemeService;

    private EligibilityEngineService engineService;

    @BeforeEach
    void setUp() {
        engineService = new EligibilityEngineService(schemeRepository, schemeService);

        lenient().when(schemeService.toDto(any(Scheme.class))).thenAnswer(invocation -> {
            Scheme s = invocation.getArgument(0);
            SchemeDto dto = new SchemeDto();
            dto.setId(s.getId());
            dto.setNameEn(s.getNameEn());
            dto.setNameTa(s.getNameTa());
            dto.setBenefitAmount(s.getBenefitAmount());
            dto.setBenefitType(s.getBenefitType());
            return dto;
        });
    }

    // Helper to create scheme
    private Scheme createMockScheme(Long id, String name, String eduLevel, SchemeEligibilityRule rule) {
        Scheme s = new Scheme();
        s.setId(id);
        s.setNameEn(name);
        s.setNameTa(name + " (TA)");
        s.setBenefitAmount(10000.0);
        s.setBenefitType("Scholarship");
        s.setStatus("ACTIVE");

        SchemeEducationLevel sel = new SchemeEducationLevel(s, eduLevel);
        s.setEducationLevels(List.of(sel));
        s.setEligibilityRule(rule);
        return s;
    }

    // ==========================================
    // 1. PRIMARY SCHOOL TESTS
    // ==========================================

    @Test
    @DisplayName("Case 1: Primary School - CM Breakfast Scheme eligible for government school child")
    void testPrimarySchool_CMBreakfast_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMinAge(5);
        rule.setMaxAge(11);
        rule.setInstitutionType("Government");
        rule.setState("Tamil Nadu");
        rule.setGovtSchoolOnly(true);

        Scheme scheme = createMockScheme(1L, "Chief Minister Breakfast Scheme", "SCHOOL_PRIMARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_PRIMARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_PRIMARY");
        profile.setDateOfBirth(LocalDate.now().minusYears(8));
        profile.setInstitutionType("Government");
        profile.setState("Tamil Nadu");

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
        assertTrue(results.get(0).getReasons().stream().anyMatch(r -> r.contains("Government")));
    }

    @Test
    @DisplayName("Case 2: Primary School - Ineligible when studying in Private School")
    void testPrimarySchool_CMBreakfast_IneligibleInPrivateSchool() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setInstitutionType("Government");
        rule.setGovtSchoolOnly(true);

        Scheme scheme = createMockScheme(1L, "Chief Minister Breakfast Scheme", "SCHOOL_PRIMARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_PRIMARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_PRIMARY");
        profile.setInstitutionType("Private");
        profile.setState("Tamil Nadu");

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertEquals(1, results.size());
        assertFalse(results.get(0).isEligible());
        assertTrue(results.get(0).getMatchScore() < 100.0);
        assertTrue(results.get(0).getUnmetCriteria().stream().anyMatch(r -> r.contains("Government")));
    }

    // ==========================================
    // 2. MIDDLE SCHOOL TESTS
    // ==========================================

    @Test
    @DisplayName("Case 3: Middle School - NMMSS eligible when income <= 350,000 and score >= 55%")
    void testMiddleSchool_NMMSS_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMinAge(12);
        rule.setMaxAge(18);
        rule.setMaxIncome(350000.0);
        rule.setMinPercentage(55.0);

        Scheme scheme = createMockScheme(5L, "NMMSS Scholarship", "SCHOOL_MIDDLE", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_MIDDLE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_MIDDLE");
        profile.setDateOfBirth(LocalDate.now().minusYears(13));
        profile.setAnnualFamilyIncome(180000.0);
        profile.setPreviousExamPercentage(72.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 4: Middle School - NMMSS ineligible due to low marks")
    void testMiddleSchool_NMMSS_IneligibleDueToMarks() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMaxIncome(350000.0);
        rule.setMinPercentage(55.0);

        Scheme scheme = createMockScheme(5L, "NMMSS Scholarship", "SCHOOL_MIDDLE", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_MIDDLE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_MIDDLE");
        profile.setAnnualFamilyIncome(150000.0);
        profile.setPreviousExamPercentage(48.0); // Below 55%

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertEquals(1, results.size());
        assertFalse(results.get(0).isEligible());
        assertTrue(results.get(0).getUnmetCriteria().stream().anyMatch(u -> u.contains("cutoff")));
    }

    // ==========================================
    // 3. SECONDARY SCHOOL TESTS
    // ==========================================

    @Test
    @DisplayName("Case 5: Secondary School - Pre-Matric SC eligible for SC student with income <= 250,000")
    void testSecondarySchool_PreMatricSC_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMaxIncome(250000.0);
        rule.setCommunityCategory("SC,ST");

        Scheme scheme = createMockScheme(3L, "Pre-Matric SC Scholarship", "SCHOOL_SECONDARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_SECONDARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_SECONDARY");
        profile.setCommunityCategory("SC");
        profile.setAnnualFamilyIncome(120000.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 6: Secondary School - Pre-Matric SC ineligible when income exceeds 250,000")
    void testSecondarySchool_PreMatricSC_IneligibleDueToHighIncome() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMaxIncome(250000.0);
        rule.setCommunityCategory("SC,ST");

        Scheme scheme = createMockScheme(3L, "Pre-Matric SC Scholarship", "SCHOOL_SECONDARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_SECONDARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_SECONDARY");
        profile.setCommunityCategory("SC");
        profile.setAnnualFamilyIncome(380000.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertFalse(results.get(0).isEligible());
        assertTrue(results.get(0).getUnmetCriteria().stream().anyMatch(u -> u.contains("exceeds ceiling")));
    }

    @Test
    @DisplayName("Case 7: Secondary School - Begum Hazrat Mahal eligible for Minority girl")
    void testSecondarySchool_BegumHazratMahal_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Female");
        rule.setMinorityRequired(true);
        rule.setMaxIncome(200000.0);
        rule.setMinPercentage(50.0);

        Scheme scheme = createMockScheme(7L, "Begum Hazrat Mahal", "SCHOOL_SECONDARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_SECONDARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_SECONDARY");
        profile.setGender("Female");
        profile.setReligionMinorityFlag(true);
        profile.setAnnualFamilyIncome(95000.0);
        profile.setPreviousExamPercentage(68.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    // ==========================================
    // 4. HIGHER SECONDARY SCHOOL TESTS
    // ==========================================

    @Test
    @DisplayName("Case 8: Higher Secondary - CBSE Single Girl Child Scholarship eligible")
    void testHigherSecondary_SingleGirlChild_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Female");
        rule.setSingleGirlChildRequired(true);
        rule.setMinPercentage(60.0);

        Scheme scheme = createMockScheme(10L, "CBSE Single Girl Child", "SCHOOL_HIGHER_SECONDARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_HIGHER_SECONDARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_HIGHER_SECONDARY");
        profile.setGender("Female");
        profile.setSingleGirlChild(true);
        profile.setPreviousExamPercentage(82.5);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 9: Higher Secondary - CBSE Single Girl Child ineligible for male student")
    void testHigherSecondary_SingleGirlChild_IneligibleForMale() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Female");
        rule.setSingleGirlChildRequired(true);

        Scheme scheme = createMockScheme(10L, "CBSE Single Girl Child", "SCHOOL_HIGHER_SECONDARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_HIGHER_SECONDARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_HIGHER_SECONDARY");
        profile.setGender("Male");
        profile.setSingleGirlChild(false);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertFalse(results.get(0).isEligible());
        assertEquals(2, results.get(0).getUnmetCriteria().size());
    }

    @Test
    @DisplayName("Case 10: Higher Secondary - TN Free Bicycle Scheme for Class 11 Govt school")
    void testHigherSecondary_TN_FreeBicycle_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setState("Tamil Nadu");
        rule.setInstitutionType("Government,Aided");

        Scheme scheme = createMockScheme(8L, "TN Free Bicycle Scheme", "SCHOOL_HIGHER_SECONDARY", rule);
        when(schemeRepository.findByEducationLevel("SCHOOL_HIGHER_SECONDARY")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("SCHOOL_HIGHER_SECONDARY");
        profile.setState("Tamil Nadu");
        profile.setInstitutionType("Government");

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }

    // ==========================================
    // 5. DIPLOMA / ITI TESTS
    // ==========================================

    @Test
    @DisplayName("Case 11: Diploma - AICTE Pragati Scholarship for Girls eligible")
    void testDiploma_AICTE_Pragati_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Female");
        rule.setMaxIncome(800000.0);

        Scheme scheme = createMockScheme(15L, "AICTE Pragati (Diploma)", "DIPLOMA", rule);
        when(schemeRepository.findByEducationLevel("DIPLOMA")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("DIPLOMA");
        profile.setGender("Female");
        profile.setAnnualFamilyIncome(300000.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 12: Diploma - AICTE Saksham for Specially-Abled eligible")
    void testDiploma_AICTE_Saksham_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setDisabilityRequired(true);
        rule.setMaxIncome(800000.0);

        Scheme scheme = createMockScheme(16L, "AICTE Saksham (Diploma)", "DIPLOMA", rule);
        when(schemeRepository.findByEducationLevel("DIPLOMA")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("DIPLOMA");
        profile.setDisabilityStatus(true);
        profile.setAnnualFamilyIncome(250000.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }

    @Test
    @DisplayName("Case 13: Diploma - AICTE Saksham ineligible when disability is false")
    void testDiploma_AICTE_Saksham_IneligibleWithoutDisability() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setDisabilityRequired(true);

        Scheme scheme = createMockScheme(16L, "AICTE Saksham (Diploma)", "DIPLOMA", rule);
        when(schemeRepository.findByEducationLevel("DIPLOMA")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("DIPLOMA");
        profile.setDisabilityStatus(false);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertFalse(results.get(0).isEligible());
        assertTrue(results.get(0).getUnmetCriteria().stream().anyMatch(u -> u.contains("disability")));
    }

    // ==========================================
    // 6. UNDERGRADUATE (UG) TESTS
    // ==========================================

    @Test
    @DisplayName("Case 14: Undergraduate - Pudhumai Penn eligible for TN Govt School girls")
    void testUndergraduate_PudhumaiPenn_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Female");
        rule.setState("Tamil Nadu");
        rule.setGovtSchoolOnly(true);

        Scheme scheme = createMockScheme(22L, "Pudhumai Penn Scheme", "UNDERGRADUATE", rule);
        when(schemeRepository.findByEducationLevel("UNDERGRADUATE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("UNDERGRADUATE");
        profile.setGender("Female");
        profile.setState("Tamil Nadu");
        profile.setInstitutionType("Government");

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 15: Undergraduate - Tamil Pudhalvan eligible for TN Govt School boys")
    void testUndergraduate_TamilPudhalvan_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Male");
        rule.setState("Tamil Nadu");
        rule.setGovtSchoolOnly(true);

        Scheme scheme = createMockScheme(23L, "Tamil Pudhalvan Scheme", "UNDERGRADUATE", rule);
        when(schemeRepository.findByEducationLevel("UNDERGRADUATE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("UNDERGRADUATE");
        profile.setGender("Male");
        profile.setState("Tamil Nadu");
        profile.setInstitutionType("Government");

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }

    @Test
    @DisplayName("Case 16: Undergraduate - First Graduate Fee Concession eligible in TN")
    void testUndergraduate_FirstGraduate_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setState("Tamil Nadu");
        rule.setFirstGraduateRequired(true);

        Scheme scheme = createMockScheme(24L, "First Graduate Scheme", "UNDERGRADUATE", rule);
        when(schemeRepository.findByEducationLevel("UNDERGRADUATE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("UNDERGRADUATE");
        profile.setState("Tamil Nadu");
        profile.setFirstGraduate(true);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }

    @Test
    @DisplayName("Case 17: Undergraduate - INSPIRE-SHE eligible for high scoring Science student")
    void testUndergraduate_INSPIRE_SHE_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMinPercentage(85.0);
        rule.setCourseStream("Science");

        Scheme scheme = createMockScheme(25L, "INSPIRE SHE", "UNDERGRADUATE", rule);
        when(schemeRepository.findByEducationLevel("UNDERGRADUATE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("UNDERGRADUATE");
        profile.setStream("Science");
        profile.setCourse("B.Sc Physics");
        profile.setPreviousExamPercentage(91.4);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }

    // ==========================================
    // 7. POSTGRADUATE (PG) TESTS
    // ==========================================

    @Test
    @DisplayName("Case 18: Postgraduate - UGC Indira Gandhi Single Girl Child eligible")
    void testPostgraduate_IndiraGandhi_SingleGirlChild_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setGender("Female");
        rule.setSingleGirlChildRequired(true);

        Scheme scheme = createMockScheme(33L, "UGC PG Indira Gandhi Scholarship", "POSTGRADUATE", rule);
        when(schemeRepository.findByEducationLevel("POSTGRADUATE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("POSTGRADUATE");
        profile.setGender("Female");
        profile.setSingleGirlChild(true);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 19: Postgraduate - AICTE PG Scholarship for GATE/GPAT eligible")
    void testPostgraduate_AICTE_GATE_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMinPercentage(60.0);
        rule.setCourseStream("Engineering,Technology,Pharmacy");

        Scheme scheme = createMockScheme(35L, "AICTE PG Scholarship", "POSTGRADUATE", rule);
        when(schemeRepository.findByEducationLevel("POSTGRADUATE")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("POSTGRADUATE");
        profile.setCourse("M.Tech Computer Science");
        profile.setStream("Engineering");
        profile.setPreviousExamPercentage(78.5);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }

    // ==========================================
    // 8. RESEARCH (MPhil/PhD) TESTS
    // ==========================================

    @Test
    @DisplayName("Case 20: Research - UGC-NET JRF eligible for candidate with >= 55% marks")
    void testResearch_UGC_NET_JRF_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setMinPercentage(55.0);
        rule.setMaxAge(35);

        Scheme scheme = createMockScheme(41L, "UGC-NET JRF", "RESEARCH", rule);
        when(schemeRepository.findByEducationLevel("RESEARCH")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("RESEARCH");
        profile.setDateOfBirth(LocalDate.now().minusYears(26));
        profile.setPreviousExamPercentage(68.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
        assertEquals(100.0, results.get(0).getMatchScore());
    }

    @Test
    @DisplayName("Case 21: Research - National Fellowship for SC Research Scholars eligible")
    void testResearch_NFSC_Eligible() {
        SchemeEligibilityRule rule = new SchemeEligibilityRule();
        rule.setCommunityCategory("SC");
        rule.setMaxIncome(800000.0);
        rule.setMinPercentage(55.0);

        Scheme scheme = createMockScheme(44L, "NFSC Research Fellowship", "RESEARCH", rule);
        when(schemeRepository.findByEducationLevel("RESEARCH")).thenReturn(List.of(scheme));

        StudentProfileDto profile = new StudentProfileDto();
        profile.setEducationLevel("RESEARCH");
        profile.setCommunityCategory("SC");
        profile.setAnnualFamilyIncome(350000.0);
        profile.setPreviousExamPercentage(64.0);

        List<SchemeMatchResultDto> results = engineService.evaluateEligibility(profile);
        assertTrue(results.get(0).isEligible());
    }
}

