package org.example.dto;

/**
 * Response DTO for delete operations.
 * Provides clear feedback for validation in load tests.
 */
public class DeleteResponse {
    private Integer id;
    private Boolean deleted;
    private String message;

    public DeleteResponse() {
    }

    public DeleteResponse(Integer id, Boolean deleted, String message) {
        this.id = id;
        this.deleted = deleted;
        this.message = message;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

