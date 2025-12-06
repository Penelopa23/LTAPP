package org.example.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for document upload.
 * Wraps MultipartFile for validation and future extensibility.
 */
public class DocUploadRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;

    public DocUploadRequest() {
    }

    public DocUploadRequest(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

