package org.example.service;

import org.example.database.entity.DocEntity;
import org.example.database.repository.DocsRepository;
import org.example.dto.DocDetailsResponse;
import org.example.dto.DocResponse;
import org.example.dto.DeleteResponse;
import org.example.dto.PageDto;
import org.example.dto.SignedDocResponse;
import org.example.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Service layer for document operations.
 * Provides business logic separated from controllers.
 */
@Service
public class DocService {

    private static final Logger logger = LoggerFactory.getLogger(DocService.class);

    private final DocsRepository docsRepository;

    @Value("${ltapp.sign.processing-delay-ms:0}")
    private long processingDelayMs;

    @Autowired
    public DocService(DocsRepository docsRepository) {
        this.docsRepository = docsRepository;
    }

    /**
     * Upload a document to the database.
     */
    @Transactional
    public DocResponse uploadDocument(MultipartFile file, String uploadedBy) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try {
            DocEntity doc = new DocEntity();
            doc.setName(file.getOriginalFilename());
            doc.setDocument(file.getBytes());
            doc.setUploadedBy(uploadedBy);
            doc.setStatus("UPLOADED");
            doc.setVersion(1);
            doc.setCreatedAt(Instant.now());
            DocEntity saved = docsRepository.save(doc);

            logger.info("Document uploaded: id={}, name={}, size={}, by={}",
                    saved.getId(), saved.getName(), file.getSize(), uploadedBy);

            return new DocResponse(
                    saved.getId(),
                    saved.getName(),
                    file.getSize(),
                    uploadedBy,
                    Instant.now(),
                    "UPLOADED"
            );
        } catch (Exception e) {
            logger.error("Error uploading document", e);
            throw new RuntimeException("Failed to upload document: " + e.getMessage(), e);
        }
    }

    /**
     * Find documents by name.
     */
    public List<DocResponse> findDocumentsByName(String name) {
        List<DocEntity> entities = docsRepository.findByName(name);
        return entities.stream()
                .map(this::toDocResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find documents by name with pagination.
     */
    public Page<DocResponse> searchDocuments(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Note: This is a simplified search. In a real app, you'd use a proper search query.
        List<DocEntity> allMatching = docsRepository.findByName(name);
        // For now, we'll return all results in a page. A proper implementation would use a custom query.
        List<DocResponse> responses = allMatching.stream()
                .map(this::toDocResponse)
                .collect(Collectors.toList());
        
        // This is a simplified pagination. In production, use proper JPA pagination.
        int start = page * size;
        int end = Math.min(start + size, responses.size());
        List<DocResponse> pageContent = start < responses.size() 
                ? responses.subList(start, end) 
                : List.of();
        
        return new PageImpl<>(
                pageContent,
                pageable,
                responses.size()
        );
    }

    /**
     * Get document by ID.
     */
    public DocResponse getDocumentById(Integer id) {
        DocEntity doc = docsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
        return toDocResponse(doc);
    }

    /**
     * Sign a document (simulated signing process).
     */
    @Transactional
    public SignedDocResponse signDocument(MultipartFile file, String signedBy, String signAlgorithm) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        long startTime = System.currentTimeMillis();

        // Simulate processing delay (configurable for load testing)
        if (processingDelayMs > 0) {
            try {
                Thread.sleep(processingDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            // Save the document first
            DocEntity doc = new DocEntity();
            doc.setName(file.getOriginalFilename());
            doc.setDocument(file.getBytes());
            DocEntity saved = docsRepository.save(doc);

            long processingTime = System.currentTimeMillis() - startTime;

            logger.info("Document signed: id={}, name={}, by={}, algo={}, time={}ms",
                    saved.getId(), saved.getName(), signedBy, signAlgorithm, processingTime);

            return new SignedDocResponse(
                    saved.getId(),
                    saved.getName(),
                    file.getSize(),
                    "SIGNED",
                    signedBy,
                    signAlgorithm != null ? signAlgorithm : "FAKE-RSA",
                    processingTime,
                    Instant.now()
            );
        } catch (Exception e) {
            logger.error("Error signing document", e);
            throw new RuntimeException("Failed to sign document: " + e.getMessage(), e);
        }
    }

    /**
     * Get document details by ID (extended metadata).
     */
    public DocDetailsResponse getDocumentDetails(Integer id) {
        DocEntity doc = docsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
        return toDocDetailsResponse(doc);
    }

    /**
     * Delete a document by ID.
     */
    @Transactional
    public DeleteResponse deleteDocument(Integer id) {
        DocEntity doc = docsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
        
        docsRepository.delete(doc);
        logger.info("Document deleted: id={}, name={}", id, doc.getName());
        
        return new DeleteResponse(id, true, "Document deleted successfully");
    }

    /**
     * Sign an existing document by ID.
     */
    @Transactional
    public SignedDocResponse signExistingDocument(Integer documentId, String signedBy, String signAlgorithm) {
        DocEntity doc = docsRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

        long startTime = System.currentTimeMillis();

        // Simulate processing delay
        if (processingDelayMs > 0) {
            try {
                Thread.sleep(processingDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Update document status
        doc.setStatus("SIGNED");
        doc.setVersion(doc.getVersion() + 1);
        DocEntity saved = docsRepository.save(doc);

        long processingTime = System.currentTimeMillis() - startTime;

        logger.info("Document signed: id={}, name={}, by={}, algo={}, time={}ms",
                saved.getId(), saved.getName(), signedBy, signAlgorithm, processingTime);

        return new SignedDocResponse(
                saved.getId(),
                saved.getName(),
                saved.getDocument() != null ? (long) saved.getDocument().length : 0L,
                "SIGNED",
                signedBy,
                signAlgorithm != null ? signAlgorithm : "FAKE-RSA",
                processingTime,
                Instant.now()
        );
    }

    /**
     * Generate multiple documents for datapool.
     */
    @Transactional
    public List<Integer> generateDocuments(int count, String namePrefix, int minSizeBytes, int maxSizeBytes) {
        List<Integer> generatedIds = new java.util.ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String name = namePrefix + "_" + i + ".pdf";
            int size = minSizeBytes + random.nextInt(maxSizeBytes - minSizeBytes + 1);
            byte[] content = new byte[size];
            random.nextBytes(content);

            DocEntity doc = new DocEntity();
            doc.setName(name);
            doc.setDocument(content);
            doc.setStatus("UPLOADED");
            doc.setVersion(1);
            doc.setCreatedAt(Instant.now());
            doc.setUploadedBy("system");

            DocEntity saved = docsRepository.save(doc);
            generatedIds.add(saved.getId());
        }

        logger.info("Generated {} documents with prefix: {}", count, namePrefix);
        return generatedIds;
    }

    /**
     * Get documents for datapool (lightweight list).
     */
    public List<DocResponse> getDocumentsForDatapool(Integer limit, String status, String namePrefix) {
        List<DocEntity> allDocs = new java.util.ArrayList<>();
        docsRepository.findAll().forEach(allDocs::add);

        return allDocs.stream()
                .filter(doc -> status == null || doc.getStatus().equals(status))
                .filter(doc -> namePrefix == null || doc.getName().startsWith(namePrefix))
                .limit(limit != null ? limit : 100)
                .map(this::toDocResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Page<DocResponse> to PageDto<DocResponse>.
     */
    public PageDto<DocResponse> toPageDto(Page<DocResponse> page) {
        return new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private DocResponse toDocResponse(DocEntity entity) {
        return new DocResponse(
                entity.getId(),
                entity.getName(),
                entity.getDocument() != null ? (long) entity.getDocument().length : 0L,
                entity.getUploadedBy(),
                entity.getCreatedAt(),
                entity.getStatus()
        );
    }

    private DocDetailsResponse toDocDetailsResponse(DocEntity entity) {
        return new DocDetailsResponse(
                entity.getId(),
                entity.getName(),
                entity.getDocument() != null ? (long) entity.getDocument().length : 0L,
                entity.getUploadedBy(),
                entity.getCreatedAt(),
                entity.getStatus(),
                entity.getVersion()
        );
    }
}

