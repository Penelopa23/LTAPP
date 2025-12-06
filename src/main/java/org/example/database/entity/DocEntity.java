package org.example.database.entity;


import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "documents")
public class DocEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public byte[] getDocument() {
        return document;
    }

    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(name = "document", nullable = false, columnDefinition = "bytea")
    private byte[] document;
    
    @Column(nullable = false, length = 20)
    private String status = "UPLOADED"; // UPLOADED, SIGNED
    
    @Column(nullable = false)
    private Integer version = 1;
    
    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = "UPLOADED";
        }
        if (version == null) {
            version = 1;
        }
    }

    public DocEntity() {
    }

    public DocEntity(String name, byte[] document) {
        this.name = name;
        this.document = document;
        this.status = "UPLOADED";
        this.version = 1;
        this.createdAt = Instant.now();
    }

    // Additional getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}