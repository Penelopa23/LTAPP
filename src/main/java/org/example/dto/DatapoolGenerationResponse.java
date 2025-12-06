package org.example.dto;

import java.util.List;

/**
 * Response DTO for datapool generation operations.
 * Provides feedback on generated test data for load testing.
 */
public class DatapoolGenerationResponse {
    private String type; // "DOCS" or "MESSAGES"
    private Integer requestedCount;
    private Integer createdCount;
    private String namePrefix;
    private List<Integer> sampleIds; // Sample of generated IDs

    public DatapoolGenerationResponse() {
    }

    public DatapoolGenerationResponse(String type, Integer requestedCount, Integer createdCount,
                                    String namePrefix, List<Integer> sampleIds) {
        this.type = type;
        this.requestedCount = requestedCount;
        this.createdCount = createdCount;
        this.namePrefix = namePrefix;
        this.sampleIds = sampleIds;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(Integer requestedCount) {
        this.requestedCount = requestedCount;
    }

    public Integer getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(Integer createdCount) {
        this.createdCount = createdCount;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public List<Integer> getSampleIds() {
        return sampleIds;
    }

    public void setSampleIds(List<Integer> sampleIds) {
        this.sampleIds = sampleIds;
    }
}

