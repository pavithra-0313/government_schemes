package com.scholarship.finder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "scheme_education_levels")
public class SchemeEducationLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    @JsonIgnore
    private Scheme scheme;

    @Column(name = "education_level", nullable = false)
    private String educationLevel;

    public SchemeEducationLevel() {}

    public SchemeEducationLevel(Scheme scheme, String educationLevel) {
        this.scheme = scheme;
        this.educationLevel = educationLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Scheme getScheme() { return scheme; }
    public void setScheme(Scheme scheme) { this.scheme = scheme; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
}

