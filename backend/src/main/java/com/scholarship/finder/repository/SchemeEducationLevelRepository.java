package com.scholarship.finder.repository;

import com.scholarship.finder.entity.SchemeEducationLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeEducationLevelRepository extends JpaRepository<SchemeEducationLevel, Long> {
    List<SchemeEducationLevel> findBySchemeId(Long schemeId);
    void deleteBySchemeId(Long schemeId);
}

