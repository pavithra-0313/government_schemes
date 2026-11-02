package com.scholarship.finder.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_schemes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "scheme_id"})
})
public class SavedScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    @Column(name = "application_status", nullable = false)
    private String applicationStatus = "Saved"; // Saved, In Progress, Applied, Approved, Rejected

    @Column(name = "saved_at")
    private LocalDateTime savedAt = LocalDateTime.now();

    public SavedScheme() {}

    public SavedScheme(User user, Scheme scheme, String applicationStatus) {
        this.user = user;
        this.scheme = scheme;
        this.applicationStatus = applicationStatus != null ? applicationStatus : "Saved";
        this.savedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Scheme getScheme() { return scheme; }
    public void setScheme(Scheme scheme) { this.scheme = scheme; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}

