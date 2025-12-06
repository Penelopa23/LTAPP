package org.example.dto;

import java.time.Instant;

/**
 * Response DTO for signed document operations.
 * Includes signature metadata for validation in load tests.
 */
public class SignedDocResponse {
    private Integer id;
    private String name;
    private Long size;
    private String signatureStatus;
    private String signedBy;
    private String signatureAlgo;
    private Long processingTimeMs;
    private Instant signedAt;

    public SignedDocResponse() {
    }

    public SignedDocResponse(Integer id, String name, Long size, String signatureStatus,
                            String signedBy, String signatureAlgo, Long processingTimeMs, Instant signedAt) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.signatureStatus = signatureStatus;
        this.signedBy = signedBy;
        this.signatureAlgo = signatureAlgo;
        this.processingTimeMs = processingTimeMs;
        this.signedAt = signedAt;
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

    public String getSignatureStatus() {
        return signatureStatus;
    }

    public void setSignatureStatus(String signatureStatus) {
        this.signatureStatus = signatureStatus;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public String getSignatureAlgo() {
        return signatureAlgo;
    }

    public void setSignatureAlgo(String signatureAlgo) {
        this.signatureAlgo = signatureAlgo;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Instant signedAt) {
        this.signedAt = signedAt;
    }
}

