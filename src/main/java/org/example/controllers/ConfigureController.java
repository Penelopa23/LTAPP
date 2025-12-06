package org.example.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.example.dto.ApiResponse;
import org.example.service.LoadControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for load control operations (CPU and memory leak).
 * Hidden from Swagger but protected by authentication.
 * Optionally requires ADMIN role for security.
 */
@Hidden
@RestController
@RequestMapping("/api")
public class ConfigureController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigureController.class);

    private final LoadControlService loadControlService;

    @Autowired
    public ConfigureController(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }

    @Operation(summary = "Enable memory leak", description = "Starts memory leak process for load testing")
    @GetMapping(path = "/startLeak")
    @PreAuthorize("hasRole('ADMIN')") // Optional: require ADMIN role
    public ResponseEntity<ApiResponse<Map<String, String>>> enableMemoryLeak() {
        logger.info("Memory leak enabled via API");
        loadControlService.enableMemoryLeak();
        Map<String, String> data = new HashMap<>();
        data.put("status", "Memory Leak Enabled");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Disable memory leak", description = "Stops memory leak process")
    @GetMapping(path = "/stopLeak")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> disableMemoryLeak() {
        logger.info("Memory leak disabled via API");
        loadControlService.disableMemoryLeak();
        Map<String, String> data = new HashMap<>();
        data.put("status", "Memory Leak Disabled");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Enable CPU load", description = "Starts CPU load generation for load testing")
    @GetMapping(path = "/startCPULoad")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> enableCPULoad() {
        logger.info("CPU load enabled via API");
        loadControlService.enableCPULoad();
        Map<String, String> data = new HashMap<>();
        data.put("status", "CPU load Enabled");
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Disable CPU load", description = "Stops CPU load generation")
    @GetMapping(path = "/stopCPULoad")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> disableCPULoad() {
        logger.info("CPU load disabled via API");
        loadControlService.disableCPULoad();
        Map<String, String> data = new HashMap<>();
        data.put("status", "CPU load Disabled");
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
