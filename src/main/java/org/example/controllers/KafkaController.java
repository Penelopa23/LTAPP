package org.example.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.example.dto.ApiResponse;
import org.example.dto.KafkaMessageResponse;
import org.example.dto.SendMessageRequest;
import org.example.service.KafkaMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for Kafka message operations.
 * Refactored to use service layer and DTOs with consistent ApiResponse format.
 * Maintains backward compatibility with original GET endpoints.
 */
@Tag(name = "Kafka", description = "Kafka message operations")
@RestController
@RequestMapping("/api")
public class KafkaController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

    private final KafkaMessageService kafkaMessageService;

    @Autowired
    public KafkaController(KafkaMessageService kafkaMessageService) {
        this.kafkaMessageService = kafkaMessageService;
    }

    @Operation(summary = "Send message to Kafka (POST)",
               description = "Send a message to Kafka topic using JSON payload. Preferred method for new implementations.")
    @Timed("sendMessage")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202",
                              description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                              description = "Validation error")
    })
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<KafkaMessageResponse>> sendMessage(
            @Valid @RequestBody SendMessageRequest request) {
        logger.debug("Sending message to Kafka: length={}", request.getPayload().length());
        KafkaMessageResponse response = kafkaMessageService.sendMessage(request.getPayload());
        return ResponseEntity.accepted().body(ApiResponse.success(response));
    }


    @Operation(summary = "Get random message from queue",
               description = "Retrieves a random message from the internal Kafka message queue")
    @Timed("getMessage")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                              description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                              description = "No messages available in queue")
    })
    @GetMapping("/getMessage")
    public ResponseEntity<ApiResponse<KafkaMessageResponse>> getRandomMessage() {
        logger.debug("Retrieving random message from queue");
        KafkaMessageResponse response = kafkaMessageService.getNextMessage();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get random message (alternative endpoint)",
               description = "Alternative endpoint for getting random messages")
    @Timed("getMessage")
    @GetMapping("/messages/random")
    public ResponseEntity<ApiResponse<KafkaMessageResponse>> getRandomMessageAlt() {
        KafkaMessageResponse response = kafkaMessageService.getRandomMessageFromQueue();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get queue size",
               description = "Returns the current number of messages in the internal queue")
    @Timed("getQueueSize")
    @GetMapping("/messages/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQueueSize() {
        int size = kafkaMessageService.getQueueSize();
        Map<String, Object> data = new HashMap<>();
        data.put("count", size);
        data.put("queueName", "internal");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Get Kafka message statistics",
               description = "Returns statistics about Kafka messages: totalSent, totalConsumed, currentQueueSize, lastMessageTimestamp")
    @Timed("getKafkaStats")
    @GetMapping("/messages/stats")
    public ResponseEntity<ApiResponse<org.example.dto.KafkaStatsResponse>> getStats() {
        logger.debug("Retrieving Kafka statistics");
        org.example.dto.KafkaStatsResponse stats = kafkaMessageService.getStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
