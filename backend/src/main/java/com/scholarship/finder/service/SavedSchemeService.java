package com.scholarship.finder.service;

import com.scholarship.finder.dto.SavedSchemeDto;
import com.scholarship.finder.entity.Document;
import com.scholarship.finder.entity.DocumentChecklist;
import com.scholarship.finder.entity.SavedScheme;
import com.scholarship.finder.entity.Scheme;
import com.scholarship.finder.entity.User;
import com.scholarship.finder.exception.BadRequestException;
import com.scholarship.finder.exception.ResourceNotFoundException;
import com.scholarship.finder.repository.DocumentChecklistRepository;
import com.scholarship.finder.repository.SavedSchemeRepository;
import com.scholarship.finder.repository.SchemeRepository;
import com.scholarship.finder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedSchemeService {

    private final SavedSchemeRepository savedSchemeRepository;
    private final SchemeRepository schemeRepository;
    private final UserRepository userRepository;
    private final DocumentChecklistRepository checklistRepository;
    private final SchemeService schemeService;

    public SavedSchemeService(SavedSchemeRepository savedSchemeRepository,
                              SchemeRepository schemeRepository,
                              UserRepository userRepository,
                              DocumentChecklistRepository checklistRepository,
                              SchemeService schemeService) {
        this.savedSchemeRepository = savedSchemeRepository;
        this.schemeRepository = schemeRepository;
        this.userRepository = userRepository;
        this.checklistRepository = checklistRepository;
        this.schemeService = schemeService;
    }

    public List<SavedSchemeDto> getSavedSchemesForUser(Long userId) {
        return savedSchemeRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SavedSchemeDto saveScheme(Long userId, Long schemeId) {
        Optional<SavedScheme> existing = savedSchemeRepository.findByUserIdAndSchemeId(userId, schemeId);
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found: " + schemeId));

        SavedScheme savedScheme = new SavedScheme(user, scheme, "Saved");
        SavedScheme result = savedSchemeRepository.save(savedScheme);

        // Auto-initialize document checklist for all required documents of this scheme
        if (scheme.getDocuments() != null) {
            for (Document doc : scheme.getDocuments()) {
                Optional<DocumentChecklist> check = checklistRepository.findByUserIdAndSchemeIdAndDocumentId(userId, schemeId, doc.getId());
                if (check.isEmpty()) {
                    checklistRepository.save(new DocumentChecklist(user, scheme, doc, false));
                }
            }
        }

        return toDto(result);
    }

    @Transactional
    public SavedSchemeDto updateStatus(Long userId, Long schemeId, String newStatus) {
        SavedScheme savedScheme = savedSchemeRepository.findByUserIdAndSchemeId(userId, schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved scheme not found for user: " + userId + " and scheme: " + schemeId));

        List<String> validStatuses = List.of("Saved", "In Progress", "Applied", "Approved", "Rejected");
        if (!validStatuses.contains(newStatus)) {
            throw new BadRequestException("Invalid status. Allowed values: " + validStatuses);
        }

        savedScheme.setApplicationStatus(newStatus);
        SavedScheme updated = savedSchemeRepository.save(savedScheme);
        return toDto(updated);
    }

    @Transactional
    public void removeSavedScheme(Long userId, Long schemeId) {
        savedSchemeRepository.deleteByUserIdAndSchemeId(userId, schemeId);
        checklistRepository.deleteByUserIdAndSchemeId(userId, schemeId);
    }

    public boolean isSchemeSaved(Long userId, Long schemeId) {
        return savedSchemeRepository.findByUserIdAndSchemeId(userId, schemeId).isPresent();
    }

    private SavedSchemeDto toDto(SavedScheme entity) {
        SavedSchemeDto dto = new SavedSchemeDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setSchemeId(entity.getScheme().getId());
        dto.setScheme(schemeService.toDto(entity.getScheme()));
        dto.setApplicationStatus(entity.getApplicationStatus());
        dto.setSavedAt(entity.getSavedAt());
        return dto;
    }
}

