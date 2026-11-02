package com.scholarship.finder.repository;

import com.scholarship.finder.entity.DocumentChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentChecklistRepository extends JpaRepository<DocumentChecklist, Long> {
    List<DocumentChecklist> findByUserIdAndSchemeId(Long userId, Long schemeId);
    List<DocumentChecklist> findByUserId(Long userId);
    Optional<DocumentChecklist> findByUserIdAndSchemeIdAndDocumentId(Long userId, Long schemeId, Long documentId);
    void deleteByUserIdAndSchemeId(Long userId, Long schemeId);
}

