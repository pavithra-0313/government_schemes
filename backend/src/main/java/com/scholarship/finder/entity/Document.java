package com.scholarship.finder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_ta", nullable = false)
    private String nameTa;

    public Document() {}

    public Document(Long id, String nameEn, String nameTa) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameTa = nameTa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameTa() { return nameTa; }
    public void setNameTa(String nameTa) { this.nameTa = nameTa; }
}

