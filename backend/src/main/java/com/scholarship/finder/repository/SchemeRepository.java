package com.scholarship.finder.repository;

import com.scholarship.finder.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    @Query("SELECT DISTINCT s FROM Scheme s JOIN s.educationLevels el WHERE el.educationLevel = :level AND s.status = 'ACTIVE'")
    List<Scheme> findByEducationLevel(@Param("level") String level);

    @Query("SELECT DISTINCT s FROM Scheme s LEFT JOIN s.educationLevels el WHERE " +
           "(:keyword IS NULL OR LOWER(s.nameEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.nameTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.descriptionEn) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:level IS NULL OR el.educationLevel = :level) AND " +
           "(:schemeLevel IS NULL OR s.level = :schemeLevel) AND " +
           "(:benefitType IS NULL OR s.benefitType = :benefitType) AND " +
           "(:state IS NULL OR s.state = :state) AND " +
           "(:department IS NULL OR s.department = :department) AND " +
           "s.status = 'ACTIVE'")
    List<Scheme> searchSchemes(
            @Param("keyword") String keyword,
            @Param("level") String level,
            @Param("schemeLevel") String schemeLevel,
            @Param("benefitType") String benefitType,
            @Param("state") String state,
            @Param("department") String department
    );

    List<Scheme> findByStatus(String status);

    @Query("SELECT s FROM Scheme s WHERE s.lastDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE'")
    List<Scheme> findSchemesExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

