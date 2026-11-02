package com.scholarship.finder.service;

import com.scholarship.finder.dto.DocumentChecklistDto;
import com.scholarship.finder.entity.Document;
import com.scholarship.finder.entity.DocumentChecklist;
import com.scholarship.finder.entity.Scheme;
import com.scholarship.finder.entity.User;
import com.scholarship.finder.exception.ResourceNotFoundException;
import com.scholarship.finder.repository.DocumentChecklistRepository;
import com.scholarship.finder.repository.DocumentRepository;
import com.scholarship.finder.repository.SchemeRepository;
import com.scholarship.finder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentChecklistService {

    private final DocumentChecklistRepository checklistRepository;
    private final SchemeRepository schemeRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public DocumentChecklistService(DocumentChecklistRepository checklistRepository,
                                  SchemeRepository schemeRepository,
                                  UserRepository userRepository,
                                  DocumentRepository documentRepository) {
        this.checklistRepository = checklistRepository;
        this.schemeRepository = schemeRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    public List<DocumentChecklistDto> getChecklistForScheme(Long userId, Long schemeId) {
        return checklistRepository.findByUserIdAndSchemeId(userId, schemeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DocumentChecklistDto> getAllChecklistsForUser(Long userId) {
        return checklistRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentChecklistDto updateDocumentReady(Long userId, Long schemeId, Long documentId, boolean isReady) {
        Optional<DocumentChecklist> opt = checklistRepository.findByUserIdAndSchemeIdAndDocumentId(userId, schemeId, documentId);

        DocumentChecklist item;
        if (opt.isPresent()) {
            item = opt.get();
            item.setIsReady(isReady);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            Scheme scheme = schemeRepository.findById(schemeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Scheme not found: " + schemeId));
            Document doc = documentRepository.findById(documentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

            item = new DocumentChecklist(user, scheme, doc, isReady);
        }

        DocumentChecklist saved = checklistRepository.save(item);
        return toDto(saved);
    }

    private DocumentChecklistDto toDto(DocumentChecklist entity) {
        DocumentChecklistDto dto = new DocumentChecklistDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setSchemeId(entity.getScheme().getId());
        dto.setSchemeNameEn(entity.getScheme().getNameEn());
        dto.setSchemeNameTa(entity.getScheme().getNameTa());
        dto.setDocument(entity.getDocument());
        dto.setIsReady(entity.getIsReady());
        return dto;
    }
}

