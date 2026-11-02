package com.scholarship.finder.repository;

import com.scholarship.finder.entity.SavedScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedSchemeRepository extends JpaRepository<SavedScheme, Long> {
    List<SavedScheme> findByUserId(Long userId);
    Optional<SavedScheme> findByUserIdAndSchemeId(Long userId, Long schemeId);
    void deleteByUserIdAndSchemeId(Long userId, Long schemeId);

    @Query("SELECT ss FROM SavedScheme ss JOIN FETCH ss.scheme s JOIN FETCH ss.user u " +
           "WHERE s.lastDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE'")
    List<SavedScheme> findSavedSchemesExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ss FROM SavedScheme ss JOIN FETCH ss.scheme s JOIN FETCH ss.user u " +
           "WHERE s.renewalRequired = true AND s.status = 'ACTIVE'")
    List<SavedScheme> findSavedSchemesRequiringRenewal();
}

