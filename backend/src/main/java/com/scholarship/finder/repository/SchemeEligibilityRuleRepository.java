package com.scholarship.finder.repository;

import com.scholarship.finder.entity.SchemeEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemeEligibilityRuleRepository extends JpaRepository<SchemeEligibilityRule, Long> {
    Optional<SchemeEligibilityRule> findBySchemeId(Long schemeId);
    void deleteBySchemeId(Long schemeId);
}

