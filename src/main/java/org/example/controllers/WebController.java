package org.example.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ApiResponse;
import org.example.dto.SignedDocResponse;
import org.example.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for document signing operations.
 * Refactored to use service layer and DTOs with consistent ApiResponse format.
 */
@Tag(name = "Signing", description = "Document signing operations")
@RestController
@RequestMapping("/api")
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    private final DocService docService;

    @Autowired
    public WebController(DocService docService) {
        this.docService = docService;
    }

    @Operation(summary = "Sign document",
               description = "Uploads a file, simulates signing process, and returns signed document metadata. " +
                           "Processing delay can be configured for load testing scenarios.")
    @Timed("testSIGNDOC")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                              description = "Document signed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                              description = "Validation error or file is empty")
    })
    @PostMapping(value = "/signDoc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SignedDocResponse>> signDocument(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "signAlgorithm", required = false) String signAlgorithm,
            Authentication authentication) {
        
        String signedBy = authentication != null ? authentication.getName() : "anonymous";
        
        logger.info("Signing document: name={}, size={}, by={}, algo={}",
                   file.getOriginalFilename(), file.getSize(), signedBy, signAlgorithm);
        
        SignedDocResponse response = docService.signDocument(file, signedBy, signAlgorithm);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
