package org.example.service;

import org.example.dto.DatapoolGenerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for admin datapool generation operations.
 * Allows generating test data for load testing scenarios.
 */
@Service
public class AdminDataService {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataService.class);

    private final DocService docService;
    private final KafkaMessageService kafkaMessageService;

    @Autowired
    public AdminDataService(DocService docService, KafkaMessageService kafkaMessageService) {
        this.docService = docService;
        this.kafkaMessageService = kafkaMessageService;
    }

    /**
     * Generate documents for datapool.
     */
    @Transactional
    public DatapoolGenerationResponse generateDocuments(int count, String namePrefix,
                                                         int minSizeBytes, int maxSizeBytes) {
        logger.info("Generating {} documents with prefix: {}", count, namePrefix);
        
        List<Integer> generatedIds = docService.generateDocuments(count, namePrefix, minSizeBytes, maxSizeBytes);
        
        // Return sample IDs (first 10 or all if less)
        List<Integer> sampleIds = generatedIds.size() > 10 
                ? generatedIds.subList(0, 10) 
                : generatedIds;

        return new DatapoolGenerationResponse(
                "DOCS",
                count,
                generatedIds.size(),
                namePrefix,
                sampleIds
        );
    }

    /**
     * Generate Kafka messages for datapool.
     */
    public DatapoolGenerationResponse generateMessages(int count, String pattern) {
        logger.info("Generating {} messages with pattern: {}", count, pattern);
        
        String messagePattern = pattern != null ? pattern : "test_message_{index}_{random}";
        List<String> generatedMessages = kafkaMessageService.generateMessages(count, messagePattern);
        
        // Convert message list to IDs (use indices as IDs for simplicity)
        List<Integer> sampleIds = new ArrayList<>();
        int sampleSize = Math.min(10, generatedMessages.size());
        for (int i = 0; i < sampleSize; i++) {
            sampleIds.add(i);
        }

        return new DatapoolGenerationResponse(
                "MESSAGES",
                count,
                generatedMessages.size(),
                messagePattern,
                sampleIds
        );
    }
}

