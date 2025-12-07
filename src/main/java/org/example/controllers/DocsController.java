package org.example.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.dto.*;
import org.example.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Extended REST controller for document operations.
 * Provides rich endpoints for load testing scenarios with validation-friendly responses.
 */
@Tag(name = "Documents", description = "Extended document management operations")
@RestController
@RequestMapping("/api/docs")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class DocsController {

    private static final Logger logger = LoggerFactory.getLogger(DocsController.class);

    private final DocService docService;

    @Autowired
    public DocsController(DocService docService) {
        this.docService = docService;
    }

    @Operation(summary = "Upload document",
               description = "Upload a new document. Returns metadata with status='UPLOADED' for validation.")
    @Timed("uploadDoc")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                              description = "Document uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                              description = "Validation error or file is empty")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocDetailsResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String uploadedBy = authentication != null ? authentication.getName() : "anonymous";
        logger.info("Uploading document: name={}, size={}, by={}", 
                   file.getOriginalFilename(), file.getSize(), uploadedBy);
        
        DocResponse docResponse = docService.uploadDocument(file, uploadedBy);
        
        // Convert to DocDetailsResponse with status
        DocDetailsResponse details = new DocDetailsResponse(
                docResponse.getId(),
                docResponse.getName(),
                docResponse.getSize(),
                docResponse.getUploadedBy(),
                docResponse.getCreatedAt(),
                "UPLOADED",
                1
        );
        
        return ResponseEntity.ok(ApiResponse.success(details));
    }
    @Operation(summary = "Get document by ID",
    description = "Retrieve full metadata for a specific document. Includes status and version for validation.")
    @Timed("getDocById")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocDetailsResponse>> getDocument(@PathVariable("id") Integer id) {
        logger.debug("Getting document details: id={}", id);
        DocDetailsResponse doc = docService.getDocumentDetails(id);
        return ResponseEntity.ok(ApiResponse.success(doc));
    }

    @Operation(summary = "Search documents with pagination",
               description = "Search documents by name with pagination. Returns PageDto for validation.")
    @Timed("searchDocs")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageDto<DocResponse>>> searchDocuments(
            @RequestParam(value = "name") @NotBlank(message = "Name parameter is required") String name,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        logger.debug("Searching documents: name={}, page={}, size={}", name, page, size);
        var pageResult = docService.searchDocuments(name, page, size);
        PageDto<DocResponse> pageDto = docService.toPageDto(pageResult);
        return ResponseEntity.ok(ApiResponse.success(pageDto));
    }

    @Operation(summary = "Delete document",
               description = "Delete a document by ID. Returns DeleteResponse with deleted flag for validation.")
    @Timed("deleteDoc")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteDocument(@PathVariable("id") Integer id) {
        logger.info("Deleting document: id={}", id);
        DeleteResponse response = docService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Sign existing document",
               description = "Sign an existing document by ID. Updates status to 'SIGNED' and increments version. " +
                           "Returns SignedDocResponse with processingTimeMs for validation.")
    @Timed("signExistingDoc")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                              description = "Document signed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                              description = "Document not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                              description = "Validation error")
    })
    @PostMapping("/{id}/sign")
    public ResponseEntity<ApiResponse<SignedDocResponse>> signExistingDocument(
            @PathVariable("id") Integer id,
            @RequestBody(required = false) SignExistingDocRequest request,
            @RequestParam(value = "signAlgorithm", required = false) String signAlgorithmParam,
            Authentication authentication) {
        String signedBy = authentication != null ? authentication.getName() : "anonymous";
        String signAlgorithm = (request != null && request.getSignAlgorithm() != null) 
                ? request.getSignAlgorithm() 
                : signAlgorithmParam;
        
        logger.info("Signing existing document: id={}, by={}, algo={}", id, signedBy, signAlgorithm);
        
        SignedDocResponse response = docService.signExistingDocument(id, signedBy, signAlgorithm);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

