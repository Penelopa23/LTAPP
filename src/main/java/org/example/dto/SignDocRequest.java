package org.example.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for document signing.
 * Can be extended with metadata like userId, algorithm, etc.
 */
public class SignDocRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;
    
    private String signAlgorithm; // Optional: e.g., "FAKE-RSA", "FAKE-ECDSA"

    public SignDocRequest() {
    }

    public SignDocRequest(MultipartFile file, String signAlgorithm) {
        this.file = file;
        this.signAlgorithm = signAlgorithm;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }
}

