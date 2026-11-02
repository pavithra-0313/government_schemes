package com.scholarship.finder.service;

import com.scholarship.finder.dto.CalendarEventDto;
import com.scholarship.finder.dto.SchemeMatchResultDto;
import com.scholarship.finder.dto.StudentProfileDto;
import com.scholarship.finder.entity.SavedScheme;
import com.scholarship.finder.entity.Scheme;
import com.scholarship.finder.repository.SavedSchemeRepository;
import com.scholarship.finder.repository.SchemeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final SchemeRepository schemeRepository;
    private final SavedSchemeRepository savedSchemeRepository;
    private final StudentProfileService profileService;
    private final EligibilityEngineService eligibilityEngine;

    public CalendarService(SchemeRepository schemeRepository,
                           SavedSchemeRepository savedSchemeRepository,
                           StudentProfileService profileService,
                           EligibilityEngineService eligibilityEngine) {
        this.schemeRepository = schemeRepository;
        this.savedSchemeRepository = savedSchemeRepository;
        this.profileService = profileService;
        this.eligibilityEngine = eligibilityEngine;
    }

    public List<CalendarEventDto> getCalendarEventsForUser(Long userId) {
        Set<Long> savedSchemeIds = new HashSet<>();
        if (userId != null) {
            savedSchemeIds = savedSchemeRepository.findByUserId(userId).stream()
                    .map(ss -> ss.getScheme().getId())
                    .collect(Collectors.toSet());
        }

        List<Scheme> candidateSchemes;
        if (userId != null && profileService.hasProfile(userId)) {
            StudentProfileDto profile = profileService.getProfileByUserId(userId);
            List<SchemeMatchResultDto> matched = eligibilityEngine.evaluateEligibility(profile);
            Set<Long> matchedIds = matched.stream().map(m -> m.getScheme().getId()).collect(Collectors.toSet());
            candidateSchemes = schemeRepository.findAll().stream()
                    .filter(s -> matchedIds.contains(s.getId()))
                    .collect(Collectors.toList());
        } else {
            candidateSchemes = schemeRepository.findByStatus("ACTIVE");
        }

        LocalDate today = LocalDate.now();
        List<CalendarEventDto> events = new ArrayList<>();

        for (Scheme s : candidateSchemes) {
            boolean isSaved = savedSchemeIds.contains(s.getId());

            // 1. Closing deadline event
            if (s.getLastDate() != null) {
                CalendarEventDto closeEvent = new CalendarEventDto();
                closeEvent.setSchemeId(s.getId());
                closeEvent.setSchemeNameEn(s.getNameEn());
                closeEvent.setSchemeNameTa(s.getNameTa());
                closeEvent.setEventType("CLOSING");
                closeEvent.setDate(s.getLastDate());
                closeEvent.setDaysRemaining(ChronoUnit.DAYS.between(today, s.getLastDate()));
                closeEvent.setApplyLink(s.getApplyLink());
                closeEvent.setBenefitAmount(s.getBenefitAmount());
                closeEvent.setBenefitType(s.getBenefitType());
                closeEvent.setSaved(isSaved);
                events.add(closeEvent);
            }

            // 2. Opening event
            if (s.getOpenDate() != null && s.getOpenDate().isAfter(today.minusDays(30))) {
                CalendarEventDto openEvent = new CalendarEventDto();
                openEvent.setSchemeId(s.getId());
                openEvent.setSchemeNameEn(s.getNameEn());
                openEvent.setSchemeNameTa(s.getNameTa());
                openEvent.setEventType("OPENING");
                openEvent.setDate(s.getOpenDate());
                openEvent.setDaysRemaining(ChronoUnit.DAYS.between(today, s.getOpenDate()));
                openEvent.setApplyLink(s.getApplyLink());
                openEvent.setBenefitAmount(s.getBenefitAmount());
                openEvent.setBenefitType(s.getBenefitType());
                openEvent.setSaved(isSaved);
                events.add(openEvent);
            }

            // 3. Renewal required marker
            if (Boolean.TRUE.equals(s.getRenewalRequired()) && s.getLastDate() != null) {
                CalendarEventDto renewEvent = new CalendarEventDto();
                renewEvent.setSchemeId(s.getId());
                renewEvent.setSchemeNameEn(s.getNameEn() + " (Renewal)");
                renewEvent.setSchemeNameTa(s.getNameTa() + " (புதுப்பித்தல்)");
                renewEvent.setEventType("RENEWAL");
                renewEvent.setDate(s.getLastDate().minusDays(15));
                renewEvent.setDaysRemaining(ChronoUnit.DAYS.between(today, renewEvent.getDate()));
                renewEvent.setApplyLink(s.getApplyLink());
                renewEvent.setBenefitAmount(s.getBenefitAmount());
                renewEvent.setBenefitType(s.getBenefitType());
                renewEvent.setSaved(isSaved);
                events.add(renewEvent);
            }
        }

        // Sort by date ascending
        events.sort(Comparator.comparing(CalendarEventDto::getDate));
        return events;
    }
}

