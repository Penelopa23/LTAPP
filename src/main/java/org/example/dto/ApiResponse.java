package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard API response envelope for consistent responses across all endpoints.
 * This helps students validate responses in load tests beyond just HTTP status codes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private List<ErrorDetail> errors;
    private Instant timestamp;

    public ApiResponse() {
        this.timestamp = Instant.now();
        this.errors = new ArrayList<>();
    }

    public ApiResponse(boolean success, T data) {
        this();
        this.success = success;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>(false, null);
        response.addError(code, message, null);
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message, String field) {
        ApiResponse<T> response = new ApiResponse<>(false, null);
        response.addError(code, message, field);
        return response;
    }

    public void addError(String code, String message, String field) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new ErrorDetail(code, message, field));
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Error detail for validation and business errors
     */
    public static class ErrorDetail {
        private String code;
        private String message;
        private String field;

        public ErrorDetail() {
        }

        public ErrorDetail(String code, String message, String field) {
            this.code = code;
            this.message = message;
            this.field = field;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}

