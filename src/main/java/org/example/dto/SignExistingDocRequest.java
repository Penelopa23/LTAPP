package org.example.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for signing an existing document by ID.
 */
public class SignExistingDocRequest {
    @NotNull(message = "Document ID is required")
    private Integer documentId;
    
    private String signAlgorithm; // Optional: e.g., "FAKE-RSA", "FAKE-ECDSA"
    private String comment; // Optional comment

    public SignExistingDocRequest() {
    }

    public SignExistingDocRequest(Integer documentId, String signAlgorithm, String comment) {
        this.documentId = documentId;
        this.signAlgorithm = signAlgorithm;
        this.comment = comment;
    }

    // Getters and Setters
    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

