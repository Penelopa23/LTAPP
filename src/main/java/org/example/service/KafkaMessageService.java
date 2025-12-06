package org.example.service;

import org.example.dto.KafkaMessageResponse;
import org.example.dto.KafkaStatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service layer for Kafka message operations.
 * Handles sending and receiving messages for load testing scenarios.
 */
@Service
public class KafkaMessageService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageService.class);

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    
    // Statistics tracking
    private volatile long totalSent = 0;
    private volatile long totalConsumed = 0;
    private volatile Instant lastMessageTimestamp;

    @Value("${spring.kafka.template.default-topic}")
    private String topicName;

    @Autowired
    public KafkaMessageService(KafkaTemplate<Long, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send a message to Kafka topic.
     */
    public KafkaMessageResponse sendMessage(String payload) {
        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException("Message payload cannot be empty");
        }

        try {
            String messageId = UUID.randomUUID().toString();
            kafkaTemplate.send(topicName, payload);
            
            totalSent++;
            lastMessageTimestamp = Instant.now();

            logger.info("Message sent to Kafka: id={}, topic={}, length={}",
                    messageId, topicName, payload.length());

            return new KafkaMessageResponse(
                    messageId,
                    payload,
                    payload.length(),
                    "ENQUEUED",
                    topicName,
                    Instant.now()
            );
        } catch (KafkaException e) {
            logger.error("Failed to send message to Kafka", e);
            throw new RuntimeException("Failed to send message: " + e.getMessage(), e);
        }
    }

    public KafkaMessageResponse getNextMessage() {
        String msg = messages.poll(); // именно poll(), он забирает и удаляет
        if (msg == null) {
            throw new org.example.exception.EntityNotFoundException("No messages available in queue");
        }
        return new KafkaMessageResponse(
            UUID.randomUUID().toString(),
            msg,
            msg.length(),
            "RETRIEVED",
            topicName,
            Instant.now()
        );
    }

    /**
     * Get a random message from the internal queue.
     */
    public KafkaMessageResponse getRandomMessageFromQueue() {
        if (messages.isEmpty()) {
            throw new org.example.exception.EntityNotFoundException("No messages available in queue");
        }

        // Thread-safe random selection
        Iterator<String> iterator = messages.iterator();
        String randomMessage = null;
        Random random = new Random();
        int count = 0;
        int selectedIndex = 0;

        while (iterator.hasNext()) {
            String message = iterator.next();
            count++;
            if (random.nextInt(count) == 0) {
                randomMessage = message;
                selectedIndex = count - 1;
            }
        }

        if (randomMessage == null) {
            throw new org.example.exception.EntityNotFoundException("Failed to retrieve random message");
        }

        boolean removed = messages.remove(randomMessage);
        if (!removed) {
        throw new org.example.exception.EntityNotFoundException("Failed to retrieve random message");
        }

        return new KafkaMessageResponse(
                UUID.randomUUID().toString(), // Generate ID for response
                randomMessage,
                randomMessage.length(),
                "RETRIEVED",
                topicName,
                Instant.now()
        );
    }

    /**
     * Get the current queue size.
     */
    public int getQueueSize() {
        return messages.size();
    }

    /**
     * Get Kafka message statistics.
     */
    public KafkaStatsResponse getStats() {
        return new KafkaStatsResponse(
                totalSent,
                totalConsumed,
                messages.size(),
                lastMessageTimestamp
        );
    }

    /**
     * Generate multiple messages for datapool.
     */
    public List<String> generateMessages(int count, String pattern) {
        List<String> generatedMessages = new java.util.ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String payload = pattern.replace("{index}", String.valueOf(i))
                    .replace("{random}", String.valueOf(random.nextInt(10000)));
            
            // Send to Kafka
            try {
                kafkaTemplate.send(topicName, payload);
                totalSent++;
                generatedMessages.add(payload);
            } catch (Exception e) {
                logger.warn("Failed to send generated message: {}", e.getMessage());
            }
        }

        lastMessageTimestamp = Instant.now();
        logger.info("Generated {} messages with pattern: {}", count, pattern);
        return generatedMessages;
    }

    /**
     * Get messages for datapool (lightweight list).
     */
    public List<org.example.dto.KafkaMessagePreview> getMessagesForDatapool(Integer limit) {
        return messages.stream()
                .limit(limit != null ? limit : 100)
                .map(msg -> new org.example.dto.KafkaMessagePreview(
                        UUID.randomUUID().toString(),
                        msg.length() > 50 ? msg.substring(0, 50) + "..." : msg,
                        Instant.now()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Kafka listener that stores messages in the internal queue.
     */
    @KafkaListener(topics = "${spring.kafka.template.default-topic}", 
                   groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        messages.add(message);
        totalConsumed++;
        lastMessageTimestamp = Instant.now();
        logger.debug("Message received and queued: length={}", message.length());
    }
}

