package org.example.service;

import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class MemoryLeak {


    private static final Long SLEEP_MILLIS = 100L;
    private static final List<byte[]> HOLDER = new ArrayList<>();

    public static class Leak implements Runnable {
        private static boolean leak;
        private volatile boolean leakFlag; // Флаг для остановки утечки

        public Leak(int numIter, boolean memoryLeak) {
            leak = memoryLeak;
        }

        public void setLeakFlagLeak(boolean on) {
            leakFlag = on;
        }

        public void stopLeak() {
            leakFlag = false; // Устанавливаем флаг остановки утечки
        }

        @Override
        public void run() {
            System.out.println("Leak thread started");
            while (leakFlag && leak) { // Проверяем и флаг, и условие утечки
                    // 1 МБ за итерацию
                    byte[] chunk = new byte[1024 * 1024];
                    HOLDER.add(chunk);
                    try {
                        Thread.sleep(SLEEP_MILLIS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
            }
            System.out.println("Leak thread finished");
        }
    }

}
