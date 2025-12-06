package org.example.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.example.dto.ApiResponse;
import org.example.dto.DatapoolGenerationResponse;
import org.example.dto.DocResponse;
import org.example.dto.KafkaMessagePreview;
import org.example.service.AdminDataService;
import org.example.service.DocService;
import org.example.service.KafkaMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for datapool generation and management.
 * Allows generating test data for load testing scenarios.
 * Requires authentication (optionally ADMIN role).
 */
@Tag(name = "Admin Data Pools", description = "Generate and manage test data for load testing")
@RestController
@RequestMapping("/api/admin/datapools")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class AdminDataController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataController.class);

    private final AdminDataService adminDataService;
    private final DocService docService;
    private final KafkaMessageService kafkaMessageService;

    @Autowired
    public AdminDataController(AdminDataService adminDataService, DocService docService,
                              KafkaMessageService kafkaMessageService) {
        this.adminDataService = adminDataService;
        this.docService = docService;
        this.kafkaMessageService = kafkaMessageService;
    }

    @Operation(summary = "Generate documents for datapool",
               description = "Generates multiple documents with random content for load testing. " +
                           "Returns DatapoolGenerationResponse with createdCount and sampleIds for validation.")
    @Timed("generateDocs")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                              description = "Documents generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                              description = "Validation error")
    })
    @PostMapping("/docs")
    @PreAuthorize("hasRole('ADMIN')") // Optional: can be removed for teaching purposes
    public ResponseEntity<ApiResponse<DatapoolGenerationResponse>> generateDocuments(
            @RequestParam(value = "count") @Min(1) int count,
            @RequestParam(value = "namePrefix", defaultValue = "test_doc_") String namePrefix,
            @RequestParam(value = "minSizeBytes", defaultValue = "1024") @Min(1) int minSizeBytes,
            @RequestParam(value = "maxSizeBytes", defaultValue = "10240") @Min(1) int maxSizeBytes) {
        logger.info("Generating {} documents with prefix: {}", count, namePrefix);
        DatapoolGenerationResponse response = adminDataService.generateDocuments(
                count, namePrefix, minSizeBytes, maxSizeBytes);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get documents for datapool",
               description = "Returns a list of documents suitable for building external datapools. " +
                           "Students can use this to fetch IDs/names for their load tests.")
    @Timed("getDocsDatapool")
    @GetMapping("/docs")
    public ResponseEntity<ApiResponse<List<DocResponse>>> getDocumentsForDatapool(
            @RequestParam(value = "limit", defaultValue = "100") @Min(1) Integer limit,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "namePrefix", required = false) String namePrefix) {
        logger.debug("Getting documents for datapool: limit={}, status={}, prefix={}", 
                    limit, status, namePrefix);
        List<DocResponse> docs = docService.getDocumentsForDatapool(limit, status, namePrefix);
        return ResponseEntity.ok(ApiResponse.success(docs));
    }

    @Operation(summary = "Generate Kafka messages for datapool",
               description = "Generates multiple messages and sends them to Kafka. " +
                           "Returns DatapoolGenerationResponse with createdCount for validation.")
    @Timed("generateMessages")
    @PostMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DatapoolGenerationResponse>> generateMessages(
            @RequestParam(value = "count") @Min(1) int count,
            @RequestParam(value = "pattern", defaultValue = "test_message_{index}_{random}") String pattern) {
        logger.info("Generating {} messages with pattern: {}", count, pattern);
        DatapoolGenerationResponse response = adminDataService.generateMessages(count, pattern);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get messages for datapool",
               description = "Returns a list of messages suitable for building external datapools. " +
                           "Students can use this to fetch payloads for their load tests.")
    @Timed("getMessagesDatapool")
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<KafkaMessagePreview>>> getMessagesForDatapool(
            @RequestParam(value = "limit", defaultValue = "100") @Min(1) Integer limit) {
        logger.debug("Getting messages for datapool: limit={}", limit);
        List<KafkaMessagePreview> messages = kafkaMessageService.getMessagesForDatapool(limit);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
}

