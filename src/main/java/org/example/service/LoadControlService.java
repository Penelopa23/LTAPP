package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for controlling load generation (CPU and memory leaks).
 * Used for teaching load testing scenarios.
 */
@Service
public class LoadControlService {

    private static final Logger logger = LoggerFactory.getLogger(LoadControlService.class);

    private final MemoryLeak.Leak leakProcess;

    @Value("${ltapp.load.cpu-threads:15}")
    private int cpuThreads;

    public LoadControlService() {
        this.leakProcess = new MemoryLeak.Leak(10000, true);
    }

    /**
     * Enable memory leak for testing.
     */
    public void enableMemoryLeak() {
        leakProcess.setLeakFlagLeak(true);
        Thread thread = new Thread(leakProcess);
        thread.setDaemon(true);
        thread.start();
        logger.info("Memory leak enabled");
    }

    /**
     * Disable memory leak.
     */
    public void disableMemoryLeak() {
        leakProcess.stopLeak();
        logger.info("Memory leak disabled");
    }

    /**
     * Enable CPU load.
     */
    public void enableCPULoad() {
        CPULoad.CPUStartLoad(cpuThreads);
        logger.info("CPU load enabled with {} threads", cpuThreads);
    }

    /**
     * Disable CPU load.
     */
    public void disableCPULoad() {
        CPULoad.CPUStopLoad();
        logger.info("CPU load disabled");
    }
}

