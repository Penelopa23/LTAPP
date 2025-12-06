package org.example.dto;

import java.time.Instant;

/**
 * Response DTO for document operations.
 * Provides rich data for students to validate in load tests.
 */
public class DocResponse {
    private Integer id;
    private String name;
    private Long size;
    private String uploadedBy;
    private Instant createdAt;
    private String status; // UPLOADED, SIGNED - for validation in load tests

    public DocResponse() {
    }

    public DocResponse(Integer id, String name, Long size, String uploadedBy, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
        this.status = "UPLOADED";
    }

    public DocResponse(Integer id, String name, Long size, String uploadedBy, Instant createdAt, String status) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

