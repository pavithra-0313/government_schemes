package com.scholarship.finder.dto;

import java.util.ArrayList;
import java.util.List;

public class SchemeMatchResultDto {
    private SchemeDto scheme;
    private double matchScore; // 0 to 100%
    private boolean eligible;
    private int matchedRulesCount;
    private int totalRulesCount;
    private List<String> reasons = new ArrayList<>();
    private List<String> matchedCriteria = new ArrayList<>();
    private List<String> unmetCriteria = new ArrayList<>();

    public SchemeMatchResultDto() {}

    public SchemeMatchResultDto(SchemeDto scheme, double matchScore, boolean eligible, int matchedRulesCount, int totalRulesCount, List<String> reasons, List<String> matchedCriteria, List<String> unmetCriteria) {
        this.scheme = scheme;
        this.matchScore = matchScore;
        this.eligible = eligible;
        this.matchedRulesCount = matchedRulesCount;
        this.totalRulesCount = totalRulesCount;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
        this.matchedCriteria = matchedCriteria != null ? matchedCriteria : new ArrayList<>();
        this.unmetCriteria = unmetCriteria != null ? unmetCriteria : new ArrayList<>();
    }

    public SchemeDto getScheme() { return scheme; }
    public void setScheme(SchemeDto scheme) { this.scheme = scheme; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public int getMatchedRulesCount() { return matchedRulesCount; }
    public void setMatchedRulesCount(int matchedRulesCount) { this.matchedRulesCount = matchedRulesCount; }

    public int getTotalRulesCount() { return totalRulesCount; }
    public void setTotalRulesCount(int totalRulesCount) { this.totalRulesCount = totalRulesCount; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public List<String> getMatchedCriteria() { return matchedCriteria; }
    public void setMatchedCriteria(List<String> matchedCriteria) { this.matchedCriteria = matchedCriteria; }

    public List<String> getUnmetCriteria() { return unmetCriteria; }
    public void setUnmetCriteria(List<String> unmetCriteria) { this.unmetCriteria = unmetCriteria; }
}

