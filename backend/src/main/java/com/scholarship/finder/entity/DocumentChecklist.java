package com.scholarship.finder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "document_checklist", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "scheme_id", "document_id"})
})
public class DocumentChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "is_ready")
    private Boolean isReady = false;

    public DocumentChecklist() {}

    public DocumentChecklist(User user, Scheme scheme, Document document, Boolean isReady) {
        this.user = user;
        this.scheme = scheme;
        this.document = document;
        this.isReady = isReady != null ? isReady : false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Scheme getScheme() { return scheme; }
    public void setScheme(Scheme scheme) { this.scheme = scheme; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Boolean getIsReady() { return isReady; }
    public void setIsReady(Boolean isReady) { this.isReady = isReady; }
}

