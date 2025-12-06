package org.example.dto;

import java.time.Instant;

/**
 * Extended response DTO for document details.
 * Includes status and version for workflow validation in load tests.
 */
public class DocDetailsResponse {
    private Integer id;
    private String name;
    private Long size;
    private String uploadedBy;
    private Instant createdAt;
    private String status; // UPLOADED, SIGNED
    private Integer version;

    public DocDetailsResponse() {
    }

    public DocDetailsResponse(Integer id, String name, Long size, String uploadedBy,
                             Instant createdAt, String status, Integer version) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
        this.status = status;
        this.version = version;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

