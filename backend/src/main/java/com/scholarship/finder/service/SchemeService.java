package com.scholarship.finder.service;

import com.scholarship.finder.dto.SchemeDto;
import com.scholarship.finder.entity.*;
import com.scholarship.finder.exception.ResourceNotFoundException;
import com.scholarship.finder.repository.DocumentRepository;
import com.scholarship.finder.repository.SchemeEducationLevelRepository;
import com.scholarship.finder.repository.SchemeEligibilityRuleRepository;
import com.scholarship.finder.repository.SchemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SchemeService {

    private final SchemeRepository schemeRepository;
    private final DocumentRepository documentRepository;
    private final SchemeEducationLevelRepository educationLevelRepository;
    private final SchemeEligibilityRuleRepository eligibilityRuleRepository;

    public SchemeService(SchemeRepository schemeRepository,
                         DocumentRepository documentRepository,
                         SchemeEducationLevelRepository educationLevelRepository,
                         SchemeEligibilityRuleRepository eligibilityRuleRepository) {
        this.schemeRepository = schemeRepository;
        this.documentRepository = documentRepository;
        this.educationLevelRepository = educationLevelRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
    }

    public List<SchemeDto> getAllSchemes() {
        return schemeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SchemeDto getSchemeById(Long id) {
        Scheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + id));
        return toDto(scheme);
    }

    public List<SchemeDto> searchSchemes(String keyword, String level, String schemeLevel,
                                         String benefitType, String state, String department) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String lvl = (level != null && !level.isBlank()) ? level.trim() : null;
        String slvl = (schemeLevel != null && !schemeLevel.isBlank()) ? schemeLevel.trim() : null;
        String btype = (benefitType != null && !benefitType.isBlank()) ? benefitType.trim() : null;
        String st = (state != null && !state.isBlank()) ? state.trim() : null;
        String dept = (department != null && !department.isBlank()) ? department.trim() : null;

        return schemeRepository.searchSchemes(kw, lvl, slvl, btype, st, dept).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SchemeDto createScheme(SchemeDto dto) {
        Scheme scheme = new Scheme();
        mapDtoToScheme(dto, scheme);
        Scheme saved = schemeRepository.save(scheme);

        if (dto.getEducationLevels() != null) {
            for (String eduLevel : dto.getEducationLevels()) {
                SchemeEducationLevel sel = new SchemeEducationLevel(saved, eduLevel.trim());
                educationLevelRepository.save(sel);
            }
        }

        if (dto.getEligibilityRule() != null) {
            SchemeEligibilityRule rule = dto.getEligibilityRule();
            rule.setScheme(saved);
            eligibilityRuleRepository.save(rule);
        }

        return getSchemeById(saved.getId());
    }

    @Transactional
    public SchemeDto updateScheme(Long id, SchemeDto dto) {
        Scheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + id));

        mapDtoToScheme(dto, scheme);
        schemeRepository.save(scheme);

        if (dto.getEducationLevels() != null) {
            educationLevelRepository.deleteBySchemeId(id);
            for (String eduLevel : dto.getEducationLevels()) {
                SchemeEducationLevel sel = new SchemeEducationLevel(scheme, eduLevel.trim());
                educationLevelRepository.save(sel);
            }
        }

        if (dto.getEligibilityRule() != null) {
            eligibilityRuleRepository.deleteBySchemeId(id);
            SchemeEligibilityRule rule = dto.getEligibilityRule();
            rule.setScheme(scheme);
            eligibilityRuleRepository.save(rule);
        }

        return getSchemeById(id);
    }

    @Transactional
    public void deleteScheme(Long id) {
        if (!schemeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Scheme not found with ID: " + id);
        }
        schemeRepository.deleteById(id);
    }

    private void mapDtoToScheme(SchemeDto dto, Scheme scheme) {
        scheme.setNameEn(dto.getNameEn());
        scheme.setNameTa(dto.getNameTa());
        scheme.setDescriptionEn(dto.getDescriptionEn());
        scheme.setDescriptionTa(dto.getDescriptionTa());
        scheme.setBenefits(dto.getBenefits());
        scheme.setBenefitAmount(dto.getBenefitAmount());
        scheme.setBenefitType(dto.getBenefitType());
        scheme.setDepartment(dto.getDepartment());
        scheme.setLevel(dto.getLevel());
        scheme.setState(dto.getState());
        scheme.setApplyLink(dto.getApplyLink());
        scheme.setOpenDate(dto.getOpenDate());
        scheme.setLastDate(dto.getLastDate());
        scheme.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        scheme.setRenewalRequired(dto.getRenewalRequired() != null ? dto.getRenewalRequired() : false);
    }

    public SchemeDto toDto(Scheme entity) {
        SchemeDto dto = new SchemeDto();
        dto.setId(entity.getId());
        dto.setNameEn(entity.getNameEn());
        dto.setNameTa(entity.getNameTa());
        dto.setDescriptionEn(entity.getDescriptionEn());
        dto.setDescriptionTa(entity.getDescriptionTa());
        dto.setBenefits(entity.getBenefits());
        dto.setBenefitAmount(entity.getBenefitAmount());
        dto.setBenefitType(entity.getBenefitType());
        dto.setDepartment(entity.getDepartment());
        dto.setLevel(entity.getLevel());
        dto.setState(entity.getState());
        dto.setApplyLink(entity.getApplyLink());
        dto.setOpenDate(entity.getOpenDate());
        dto.setLastDate(entity.getLastDate());
        dto.setStatus(entity.getStatus());
        dto.setRenewalRequired(entity.getRenewalRequired());

        if (entity.getEducationLevels() != null) {
            dto.setEducationLevels(entity.getEducationLevels().stream()
                    .map(SchemeEducationLevel::getEducationLevel)
                    .collect(Collectors.toList()));
        } else {
            dto.setEducationLevels(Collections.emptyList());
        }

        dto.setEligibilityRule(entity.getEligibilityRule());
        dto.setDocuments(entity.getDocuments());
        return dto;
    }
}

