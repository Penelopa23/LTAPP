package org.example.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CPULoad {

    private static volatile boolean load; // Флаг для управления выполнением цикла

    private static ExecutorService executor; // Потоковый пул для управления потоками

    public static void CPUStartLoad(int numThreads) {
        executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            load = true;
            executor.execute(new CpuTask());
        }
    }

    public static void CPUStopLoad() {
        load = false;
        executor.shutdownNow();
    }

    static class CpuTask implements Runnable {
        @Override
        public void run() {
            while (load) {
                // Нагрузим CPU, выполняя множество математических операций.
                double x = 0;
                for (int i = 0; i < 1_000_000; i++) {
                    x += Math.sin(i); // или просто x += i * 1.000001;
                }
            }
        }
    }
}
